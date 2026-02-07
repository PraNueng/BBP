<template>
  <div>
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-bold text-gray-800">
        คลาสกลุ่มรวมที่มีอยู่
        <span v-if="!isLoading" class="text-sm font-normal text-gray-600 ml-2">
          ({{ totalCount }} คลาส)
        </span>
      </h3>
    </div>

    <div v-if="isLoading" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-orange-600"></div>
      <p class="mt-2 text-gray-600">กำลังโหลด...</p>
    </div>

    <div v-else-if="classes.length === 0" class="text-center py-8 text-gray-500">
      <svg class="mx-auto h-12 w-12 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      <p>{{ hasFilters ? 'ไม่พบคลาสที่ตรงกับเงื่อนไข' : 'ยังไม่มีคลาสรายชั่วโมงแบบกลุ่ม' }}</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="cls in classes"
        :key="cls.id"
        class="bg-white border-2 border-gray-200 rounded-xl p-4 hover:border-orange-300 transition-all"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <h4 class="text-lg font-bold text-gray-800">
                {{ cls.className || `${cls.subjectName} - ${cls.subtypeName}` }}
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
              <p><span class="font-semibold">ประเภท:</span> {{ cls.subtypeName }}</p>
              
              <!-- แสดงครูหลายคน -->
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
                    ({{ cls.totalTutors }} คน)
                  </span>
                </div>
                <span v-else class="text-gray-400 italic">ยังไม่มีครู</span>
              </div>
              
              <p v-if="cls.totalStudents !== undefined">
                <span class="font-semibold">นักเรียน:</span> {{ cls.totalStudents }} คน
              </p>
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <!-- ปุ่มจัดการครู -->
            <button
              @click="openManageTutorsModal(cls)"
              class="p-2 rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition"
              title="จัดการครู"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal จัดการครู -->
    <ManageTutorsModal
      v-if="showManageTutorsModal"
      :show="showManageTutorsModal"
      :class-data="selectedClass"
      :class-type="'hourly-group'"
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
         props.filters.subtypeId || 
         props.filters.isActive !== undefined;
});

// ✅ Filter classes ฝั่ง frontend
const filterClasses = () => {
  let filtered = [...allClasses.value];

  // Filter by subject
  if (props.filters.subjectId) {
    filtered = filtered.filter(cls => cls.subjectId === props.filters.subjectId);
  }

  // Filter by grade
  if (props.filters.gradeId) {
    filtered = filtered.filter(cls => cls.gradeId === props.filters.gradeId);
  }

  // Filter by subtype
  if (props.filters.subtypeId) {
    filtered = filtered.filter(cls => cls.subtypeId === props.filters.subtypeId);
  }

  // Filter by active status
  if (props.filters.isActive !== null && props.filters.isActive !== undefined) {
    filtered = filtered.filter(cls => cls.isActive === props.filters.isActive);
  }

  totalCount.value = filtered.length;
  emit('count-update', filtered.length);

  // ✅ Pagination
  const page = props.filters.page || 1;
  const limit = props.filters.limit || 5;
  const startIndex = (page - 1) * limit;
  const endIndex = startIndex + limit;

  classes.value = filtered.slice(startIndex, endIndex);
};

const loadClasses = async () => {
  isLoading.value = true;
  try {
    // ✅ ดึงคลาสทั้งหมด (ไม่ส่ง filter ไป backend)
    const params = {};
    
    // ถ้าไม่มี filter สถานะ ให้ดึงแค่ active
    if (props.filters.isActive === null || props.filters.isActive === undefined) {
      params.active = true;
    }

    const response = await api.get('/hourly-group-classes', { params });
    allClasses.value = response.data;
    
    // ✅ Filter ฝั่ง frontend
    filterClasses();
  } catch (error) {
    console.error('Error loading hourly group classes:', error);
    emit('error', 'ไม่สามารถโหลดคลาสได้');
    emit('count-update', 0);
  } finally {
    isLoading.value = false;
  }
};

const handleToggleActive = async (cls) => {
  try {
    await api.patch(`/hourly-group-classes/${cls.id}/toggle-active`);
    emit('success', `${cls.isActive ? 'ปิด' : 'เปิด'}ใช้งานคลาสสำเร็จ!`);
    await loadClasses();
  } catch (error) {
    console.error('Error toggling active:', error);
    emit('error', 'ไม่สามารถเปลี่ยนสถานะได้');
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

// ✅ Watch filters changes
watch(() => props.filters, () => {
  filterClasses();
}, { deep: true });

// ✅ Watch refreshTrigger
watch(() => props.refreshTrigger, () => {
  loadClasses();
});

onMounted(() => {
  loadClasses();
});
</script>