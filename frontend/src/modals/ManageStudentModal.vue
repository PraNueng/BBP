<template>
  <div
    v-if="show"
    class="fixed inset-0 backdrop-blur bg-black/20 flex items-center justify-center p-4 z-50 overflow-y-auto"
    @click.self="$emit('close')"
  >
    <div class="bg-white rounded-2xl shadow-2xl max-w-5xl w-full max-h-[90vh] overflow-hidden flex flex-col my-8">
      <!-- Header -->
      <div class="bg-gradient-to-r from-indigo-800 to-blue-700 p-6 flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-white">จัดการคลาสของนักเรียน</h2>
          <p class="text-purple-100 mt-1">{{ student?.nickname || student?.firstName }} {{ student?.lastName || '' }}</p>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 hover:bg-white/20 rounded-lg transition"
        >
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Tabs -->
      <div class="border-b border-gray-200 bg-gray-50 px-6 pt-4">
        <div class="flex gap-2 overflow-x-auto pb-2">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="changeTab(tab.id)"
            :class="[
              'px-6 py-2 rounded-t-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === tab.id
                ? 'bg-white text-purple-600 border-t-2 border-purple-600'
                : 'text-gray-600 hover:text-purple-600 hover:bg-white/50'
            ]"
          >
            {{ tab.label }}
            <span
              v-if="getTabCount(tab.id) > 0"
              class="px-2 py-0.5 bg-purple-100 text-purple-700 rounded-full text-xs font-bold"
            >
              {{ getTabCount(tab.id) }}
            </span>
          </button>
        </div>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="mb-4 p-4 bg-green-50 border-l-4 border-green-500 text-green-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ errorMessage }}</span>
        </div>

        <!-- Monthly Tab -->
        <div v-show="activeTab === 'monthly'">
          <!-- Filter Bar -->
          <FilterClassManagementBar
            :subjects="subjects"
            :grades="grades"
            :subtypes="monthlySubtypes"
            class-type="monthly"
            :show-status-filter="true"
            @filter="handleMonthlyFilter"
          />

          <!-- Loading State -->
          <div v-if="isLoadingMonthly" class="text-center py-8">
            <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600"></div>
            <p class="mt-3 text-gray-600">กำลังโหลด...</p>
          </div>

          <!-- Monthly Classes List -->
          <div v-else-if="filteredMonthlyClasses.length > 0" class="space-y-3">
            <div
              v-for="cls in filteredMonthlyClasses"
              :key="cls.id"
              class="bg-gradient-to-br from-white to-purple-50 border-2 rounded-xl p-4 hover:shadow-lg transition-all"
              :class="isEnrolled('monthly', cls.id) ? 'border-green-400' : 'border-gray-200'"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h4 class="text-lg font-bold text-gray-800 mb-1">
                    {{ cls.className || `${cls.subjectName} - ${cls.subtypeName}` }}
                  </h4>
                  <div class="flex flex-wrap gap-2 text-sm text-gray-600">
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      {{ cls.subjectName }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                      </svg>
                      {{ cls.gradeName || 'ไม่ระบุ' }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                      </svg>
                      {{ cls.subtypeName }}
                    </span>
                  </div>
                </div>
                <button
                  @click="toggleEnrollment('monthly', cls.id)"
                  :disabled="isProcessing"
                  :class="[
                    'px-4 py-2 rounded-lg font-semibold transition-all',
                    isEnrolled('monthly', cls.id)
                      ? 'bg-red-500 hover:bg-red-600 text-white'
                      : 'bg-green-500 hover:bg-green-600 text-white',
                    isProcessing ? 'opacity-50 cursor-not-allowed' : ''
                  ]"
                >
                  {{ isEnrolled('monthly', cls.id) ? 'ลบออก' : 'เพิ่ม' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p class="mt-3 text-gray-500">ไม่พบคลาสรายเดือน</p>
          </div>
        </div>

        <!-- Hourly Group Tab -->
        <div v-show="activeTab === 'hourly-group'">
          <!-- Filter Bar -->
          <FilterClassManagementBar
            :subjects="subjects"
            :grades="grades"
            :subtypes="hourlyGroupSubtypes"
            class-type="hourly-group"
            :show-status-filter="true"
            @filter="handleHourlyGroupFilter"
          />

          <!-- Loading State -->
          <div v-if="isLoadingHourlyGroup" class="text-center py-8">
            <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-purple-600"></div>
            <p class="mt-3 text-gray-600">กำลังโหลด...</p>
          </div>

          <!-- Hourly Group Classes List -->
          <div v-else-if="filteredHourlyGroupClasses.length > 0" class="space-y-3">
            <div
              v-for="cls in filteredHourlyGroupClasses"
              :key="cls.id"
              class="bg-gradient-to-br from-white to-blue-50 border-2 rounded-xl p-4 hover:shadow-lg transition-all"
              :class="isEnrolled('hourly-group', cls.id) ? 'border-green-400' : 'border-gray-200'"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h4 class="text-lg font-bold text-gray-800 mb-1">
                    {{ cls.className || `${cls.subjectName} - ${cls.subtypeName}` }}
                  </h4>
                  <div class="flex flex-wrap gap-2 text-sm text-gray-600">
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                      </svg>
                      {{ cls.subjectName }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                      </svg>
                      {{ cls.gradeName || 'ไม่ระบุ' }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
                      </svg>
                      {{ cls.subtypeName }}
                    </span>
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {{ cls.hoursTarget }} ชม.
                    </span>
                  </div>
                </div>
                <button
                  @click="toggleEnrollment('hourly-group', cls.id)"
                  :disabled="isProcessing"
                  :class="[
                    'px-4 py-2 rounded-lg font-semibold transition-all',
                    isEnrolled('hourly-group', cls.id)
                      ? 'bg-red-500 hover:bg-red-600 text-white'
                      : 'bg-green-500 hover:bg-green-600 text-white',
                    isProcessing ? 'opacity-50 cursor-not-allowed' : ''
                  ]"
                >
                  {{ isEnrolled('hourly-group', cls.id) ? 'ลบออก' : 'เพิ่ม' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <p class="mt-3 text-gray-500">ไม่พบกลุ่มรายชั่วโมง</p>
          </div>
        </div>

        <!-- Hourly Individual Tab -->
        <div v-show="activeTab === 'hourly-individual'">
          <div class="bg-blue-50 border-2 border-blue-200 rounded-xl p-6 text-center">
            <svg class="mx-auto h-16 w-16 text-blue-500 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 class="text-lg font-bold text-gray-800 mb-2">คลาสรายชั่วโมงแบบเดี่ยว</h3>
            <p class="text-gray-600 mb-4">
              คลาสเดี่ยวจะถูกสร้างโดยอัตโนมัติเมื่อเพิ่มนักเรียนผ่านหน้าจัดการนักเรียนหลัก
            </p>
            <p class="text-sm text-gray-500">
              ไปที่ <strong>แก้ไขข้อมูลนักเรียน</strong> เพื่อจัดการคลาสเดี่ยว
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import api from '@/api.js';
import FilterClassManagementBar from '@/components/FilterClassManagementBar.vue';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  student: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'updated']);

const activeTab = ref('monthly');
const subjects = ref([]);
const grades = ref([]);
const monthlySubtypes = ref([]);
const hourlyGroupSubtypes = ref([]);
const successMessage = ref('');
const errorMessage = ref('');
const isProcessing = ref(false);

const isLoadingMonthly = ref(false);
const isLoadingHourlyGroup = ref(false);

// All available classes
const allMonthlyClasses = ref([]);
const allHourlyGroupClasses = ref([]);

// Enrolled classes (from student)
const enrolledMonthlyClasses = ref([]);
const enrolledHourlyGroupClasses = ref([]);

// Filters
const monthlyFilters = ref({});
const hourlyGroupFilters = ref({});

const tabs = [
  { id: 'monthly', label: 'คลาสรายเดือน' },
  { id: 'hourly-group', label: 'กลุ่มรายชั่วโมง' },
  { id: 'hourly-individual', label: 'เดี่ยวรายชั่วโมง' }
];

const filteredMonthlyClasses = computed(() => {
  let filtered = allMonthlyClasses.value;
  
  if (monthlyFilters.value.subjectId) {
    filtered = filtered.filter(c => c.subjectId === monthlyFilters.value.subjectId);
  }
  if (monthlyFilters.value.gradeId) {
    filtered = filtered.filter(c => c.gradeId === monthlyFilters.value.gradeId);
  }
  if (monthlyFilters.value.subtypeId) {
    filtered = filtered.filter(c => c.subtypeId === monthlyFilters.value.subtypeId);
  }
  
  return filtered;
});

const filteredHourlyGroupClasses = computed(() => {
  let filtered = allHourlyGroupClasses.value;
  
  if (hourlyGroupFilters.value.subjectId) {
    filtered = filtered.filter(c => c.subjectId === hourlyGroupFilters.value.subjectId);
  }
  if (hourlyGroupFilters.value.gradeId) {
    filtered = filtered.filter(c => c.gradeId === hourlyGroupFilters.value.gradeId);
  }
  if (hourlyGroupFilters.value.subtypeId) {
    filtered = filtered.filter(c => c.subtypeId === hourlyGroupFilters.value.subtypeId);
  }
  
  return filtered;
});

const getTabCount = (tabId) => {
  switch (tabId) {
    case 'monthly':
      return enrolledMonthlyClasses.value.length;
    case 'hourly-group':
      return enrolledHourlyGroupClasses.value.length;
    default:
      return 0;
  }
};

const changeTab = (tabId) => {
  activeTab.value = tabId;
};

const handleMonthlyFilter = (filters) => {
  monthlyFilters.value = { ...filters };
};

const handleHourlyGroupFilter = (filters) => {
  hourlyGroupFilters.value = { ...filters };
};

const isEnrolled = (type, classId) => {
  if (type === 'monthly') {
    return enrolledMonthlyClasses.value.some(e => e.classId === classId);
  } else if (type === 'hourly-group') {
    return enrolledHourlyGroupClasses.value.some(e => e.classId === classId);
  }
  return false;
};

const toggleEnrollment = async (type, classId) => {
  if (!props.student) return;
  
  isProcessing.value = true;
  try {
    // สร้าง classes array ใหม่
    const currentClasses = [
      ...enrolledMonthlyClasses.value.map(e => ({
        id: e.id,
        subjectId: e.subjectId,
        classType: 'MONTH',
        mode: 'GROUP',
        schedule: e.subtypeName
      })),
      ...enrolledHourlyGroupClasses.value.map(e => ({
        id: e.id,
        subjectId: e.subjectId,
        classType: 'HOUR',
        mode: 'GROUP',
        groupType: e.subtypeName,
        hours: e.hoursTarget
      }))
    ];

    const enrolled = isEnrolled(type, classId);
    
    let updatedClasses;
    if (enrolled) {
      // ลบคลาสออก
      const enrollmentToRemove = enrolledMonthlyClasses.value.find(e => e.classId === classId);
      updatedClasses = currentClasses.filter(c => c.id !== enrollmentToRemove.id);
    } else {
      // เพิ่มคลาสใหม่
      const targetClass = type === 'monthly' 
        ? allMonthlyClasses.value.find(c => c.id === classId)
        : allHourlyGroupClasses.value.find(c => c.id === classId);
        
      if (!targetClass) throw new Error('ไม่พบคลาส');
      
      updatedClasses = [
        ...currentClasses,
        type === 'monthly' ? {
          subjectId: targetClass.subjectId,
          classType: 'MONTH',
          mode: 'GROUP',
          schedule: targetClass.subtypeName
        } : {
          subjectId: targetClass.subjectId,
          classType: 'HOUR',
          mode: 'GROUP',
          groupType: targetClass.subtypeName,
          hours: targetClass.hoursTarget
        }
      ];
    }

    // ส่งไปอัพเดท
    const payload = {
      id: props.student.id,
      firstName: props.student.firstName,
      lastName: props.student.lastName,
      nickname: props.student.nickname,
      schoolName: props.student.schoolName,
      gradeId: props.student.gradeId,
      phoneStudent: props.student.phoneStudent,
      phoneParent: props.student.phoneParent,
      studyPlan: props.student.studyPlan,
      classes: updatedClasses
    };

    await api.put(`/students/${props.student.id}`, payload);
    
    // Reload enrollments
    await loadStudentEnrollments();
    
    handleSuccess(enrolled ? 'ลบนักเรียนออกจากคลาสแล้ว' : 'เพิ่มนักเรียนเข้าคลาสแล้ว');
    emit('updated');
    
  } catch (error) {
    console.error('Error toggling enrollment:', error);
    handleError(error.response?.data?.message || 'เกิดข้อผิดพลาด');
  } finally {
    isProcessing.value = false;
  }
};

const loadData = async () => {
  try {
    const [subjectsRes, gradesRes, monthlyRes, hourlyRes] = await Promise.all([
      api.get('/subjects', { params: { active: true } }),
      api.get('/grades'),
      api.get('/monthly-subtypes', { params: { active: true } }),
      api.get('/hourly-group-subtypes', { params: { active: true } })
    ]);

    subjects.value = subjectsRes.data;
    grades.value = gradesRes.data;
    monthlySubtypes.value = monthlyRes.data;
    hourlyGroupSubtypes.value = hourlyRes.data;
  } catch (error) {
    console.error('Error loading data:', error);
    handleError('ไม่สามารถโหลดข้อมูลได้');
  }
};

const loadMonthlyClasses = async () => {
  isLoadingMonthly.value = true;
  try {
    const response = await api.get('/monthly-classes', { params: { active: true } });
    allMonthlyClasses.value = response.data;
  } catch (error) {
    console.error('Error loading monthly classes:', error);
  } finally {
    isLoadingMonthly.value = false;
  }
};

const loadHourlyGroupClasses = async () => {
  isLoadingHourlyGroup.value = true;
  try {
    const response = await api.get('/hourly-group-classes', { params: { active: true } });
    allHourlyGroupClasses.value = response.data;
  } catch (error) {
    console.error('Error loading hourly group classes:', error);
  } finally {
    isLoadingHourlyGroup.value = false;
  }
};

const loadStudentEnrollments = async () => {
  if (!props.student?.id) return;
  
  try {
    const response = await api.get(`/students/${props.student.id}/with-classes`);
    const data = response.data;
    
    // ✅ เก็บข้อมูลเต็ม ไม่ใช่แค่ id
    enrolledMonthlyClasses.value = (data.monthlyEnrollments || []).map(e => ({
      id: e.id,
      classId: e.classId,
      subjectId: e.subjectId,
      subjectName: e.subjectName,
      subtypeName: e.subtypeName
    }));
    
    enrolledHourlyGroupClasses.value = (data.hourlyGroupEnrollments || []).map(e => ({
      id: e.id,
      classId: e.classId,
      subjectId: e.subjectId,
      subjectName: e.subjectName,
      subtypeName: e.subtypeName,
      hoursTarget: e.hoursTarget
    }));
    
    console.log('✅ Loaded enrollments:', {
      monthly: enrolledMonthlyClasses.value,
      hourlyGroup: enrolledHourlyGroupClasses.value
    });
  } catch (error) {
    console.error('Error loading student enrollments:', error);
  }
};

const handleSuccess = (message) => {
  successMessage.value = message;
  errorMessage.value = '';
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const handleError = (message) => {
  errorMessage.value = message;
  successMessage.value = '';
  setTimeout(() => {
    errorMessage.value = '';
  }, 5000);
};

watch(() => props.show, async (newVal) => {
  if (newVal) {
    await loadData();
    await loadMonthlyClasses();
    await loadHourlyGroupClasses();
    await loadStudentEnrollments();
    activeTab.value = 'monthly';
  }
});

onMounted(async () => {
  if (props.show) {
    await loadData();
    await loadMonthlyClasses();
    await loadHourlyGroupClasses();
    await loadStudentEnrollments();
  }
});
</script>