<template>
  <div>
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-bold text-gray-800">
        คลาส PV-เดี่ยว/PV-กลุ่ม ที่มีอยู่
        <span v-if="!isLoading" class="text-sm font-normal text-gray-600 ml-2">
          ({{ totalCount }} คลาส)
        </span>
      </h3>
    </div>

    <div v-if="isLoading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
      <p class="mt-2 text-gray-600">กำลังโหลด...</p>
    </div>

    <div v-else-if="classes.length === 0" class="text-center py-8 text-gray-500">
      <svg class="mx-auto h-12 w-12 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
      </svg>
      <p>{{ hasFilters ? 'ไม่พบคลาสที่ตรงกับเงื่อนไข' : 'ยังไม่มีคลาสตัวต่อตัว' }}</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="cls in classes"
        :key="cls.id"
        class="bg-white border-2 border-gray-200 rounded-xl p-4 hover:border-blue-300 transition-all"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <h4 class="text-lg font-bold text-gray-800">
                {{ cls.className || `${cls.subjectName} - ${cls.studentNickname || cls.studentFirstName}` }}
              </h4>
              <span
                :class="[
                  'px-2 py-1 rounded-full text-xs font-bold',
                  cls.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                ]"
              >
                {{ cls.isActive ? 'ใช้งาน' : 'ปิด' }}
              </span>
            </div>
            
            <div class="space-y-1 text-sm text-gray-600">
              <p><span class="font-semibold">วิชา:</span> {{ cls.subjectName }}</p>
              <p><span class="font-semibold">ระดับชั้น:</span> 
                <span class="px-2 py-0.5 bg-purple-100 text-purple-700 rounded text-xs font-medium">
                  {{ cls.gradeName || 'ไม่ระบุ' }}
                </span>
              </p>
              
              <!-- ✅ แสดงครูหลายคน (เหมือน HourlyGroup และ Monthly) -->
              <div>
                <span class="font-semibold">ครูผู้สอน:</span>
                <div v-if="cls.tutors && cls.tutors.length > 0" class="mt-1 flex flex-wrap gap-1">
                  <span
                    v-for="tutor in cls.tutors"
                    :key="tutor.id"
                    class="inline-flex items-center gap-1 px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs font-medium"
                  >
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    {{ tutor.nickname }}
                  </span>
                  <span class="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs font-medium">
                    ({{ cls.totalTutors || cls.tutors.length }} คน)
                  </span>
                </div>
                <span v-else class="text-gray-400 italic">ยังไม่มีครู</span>
              </div>
              
              <p>
                <span class="font-semibold">นักเรียน:</span> 
                <span class="px-2 py-0.5 bg-green-100 text-green-700 rounded text-xs font-medium">
                  {{ cls.totalStudents || 0 }} คน
                </span>
              </p>
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <button
              @click="openManageTutorsModal(cls)"
              class="p-2 rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition"
              title="จัดการครู"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <ManageTutorsModal
      v-if="showManageTutorsModal"
      :show="showManageTutorsModal"
      :class-data="selectedClass"
      :class-type="'hourly-individual'"
      @close="showManageTutorsModal = false"
      @success="handleManageTutorsSuccess"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import api from '@/api.js';
import ManageTutorsModal from '../modals/ManageTutorsModal.vue';

const props = defineProps({
  tutorId: {
    type: Number,
    required: false
  },
  filters: {
    type: Object,
    default: () => ({})
  },
  refreshTrigger: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(['success', 'error', 'count-update']);

const classes = ref([]);
const allClasses = ref([]);
const isLoading = ref(false);
const showManageTutorsModal = ref(false);
const selectedClass = ref(null);
const totalCount = ref(0);

const hasFilters = computed(() => {
  return props.filters.subjectId || 
         props.filters.gradeId || 
         props.filters.isActive !== undefined;
});

const filterClasses = () => {
  let filtered = [...allClasses.value];

  if (props.filters.subjectId) {
    filtered = filtered.filter(cls => cls.subjectId === props.filters.subjectId);
  }

  if (props.filters.gradeId) {
    filtered = filtered.filter(cls => cls.gradeId === props.filters.gradeId);
  }

  if (props.filters.className && props.filters.className.trim()) {
    const query = props.filters.className.toLowerCase();
    filtered = filtered.filter(cls => {
      const className = (cls.className || '').toLowerCase();
      return className.includes(query);
    });
  }

  totalCount.value = filtered.length;
  emit('count-update', filtered.length);

  const page = props.filters.page || 1;
  const limit = props.filters.limit || 5;
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;

  classes.value = filtered.slice(startIndex, endIndex);
};

const loadClasses = async () => {
  isLoading.value = true;
  try {
    const params = {};

    if (props.tutorId) {
      params.tutorId = props.tutorId;
    }

    if (props.filters.isActive === null || props.filters.isActive === undefined) {
      params.active = true;
    }

    const response = await api.get('/hourly-individual-classes', { params });
    allClasses.value = response.data;
    
    // ดึงข้อมูลครู + นักเรียนสำหรับแต่ละคลาส
    await Promise.all(
      allClasses.value.map(async (cls) => {
        const legacyTutor = cls.tutorId ? [{ id: cls.tutorId, nickname: cls.tutorName }] : [];

        try {
          // ดึงข้อมูลครู
          const tutorsResponse = await api.get(`/hourly-individual-classes/${cls.id}/tutors`);
          if (tutorsResponse.data && tutorsResponse.data.length > 0) {
            cls.tutors = tutorsResponse.data;
            cls.totalTutors = tutorsResponse.data.length;
          } else if (legacyTutor.length > 0) {
            cls.tutors = legacyTutor;
            cls.totalTutors = legacyTutor.length;
          } else {
            cls.tutors = [];
            cls.totalTutors = 0;
          }

          // ✅ เพิ่ม: ดึงข้อมูลนักเรียน
          const studentsResponse = await api.get(`/hourly-individual-classes/${cls.id}/students`);
          const activeStudents = studentsResponse.data.filter(s => s.isActive !== false);
          
          cls.students = activeStudents;
          cls.totalStudents = activeStudents.length;
          
          // ถ้ามีนักเรียน ให้เอา gradeName และ nickname/firstName จากนักเรียนคนแรก
          if (activeStudents.length > 0) {
            cls.gradeName = activeStudents[0].gradeName || 'ไม่ระบุ';
            cls.studentNickname = activeStudents[0].nickname;
            cls.studentFirstName = activeStudents[0].firstName;
            cls.studentLastName = activeStudents[0].lastName;
            cls.studentCode = activeStudents[0].studentCode;
          }
          
        } catch (error) {
          if (legacyTutor.length > 0) {
            cls.tutors = legacyTutor;
            cls.totalTutors = legacyTutor.length;
          } else {
            console.warn(`Cannot fetch tutors for class ${cls.id}:`, error);
            cls.tutors = [];
            cls.totalTutors = 0;
          }
          cls.students = [];
          cls.totalStudents = 0;
        }
      })
    );
    filterClasses();
  } catch (error) {
    console.error('Error loading hourly individual classes:', error);
    emit('error', 'ไม่สามารถโหลดคลาสได้');
    emit('count-update', 0);
  } finally {
    isLoading.value = false;
  }
};

const openManageTutorsModal = (cls) => {
  selectedClass.value = cls;
  showManageTutorsModal.value = true;
};

const handleManageTutorsSuccess = (message) => {
  emit('success', message);
  loadClasses();
};

watch(() => props.filters, () => {
  filterClasses();
}, { deep: true });

watch(() => props.refreshTrigger, () => {
  loadClasses();
});

onMounted(() => {
  loadClasses();
});
</script>