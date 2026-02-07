<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto" @click="handleBackdropClick">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity"></div>
    
    <!-- Modal Container -->
    <div class="flex min-h-full items-center justify-center p-4">
      <div 
        class="relative w-full max-w-6xl bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-3xl shadow-2xl transform transition-all"
        @click.stop
      >
        <!-- Animated Background -->
        <div class="absolute inset-0 overflow-hidden rounded-3xl pointer-events-none">
          <div class="absolute w-64 h-64 bg-blue-500/20 rounded-full blur-3xl -top-32 -left-32 animate-pulse"></div>
          <div class="absolute w-64 h-64 bg-indigo-500/20 rounded-full blur-3xl -bottom-32 -right-32 animate-pulse delay-1000"></div>
        </div>

        <!-- Close Button -->
        <button
          @click="$emit('close')"
          class="absolute w-12 h-10 flex items-center justify-center top-6 right-6 z-100 bg-white/10 hover:bg-white/20 rounded-full transition-all group"
        >
          <svg class="w-6 h-6 text-white group-hover:rotate-90 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>

        <!-- Loading State -->
        <div v-if="isLoading" class="flex items-center justify-center py-24">
          <div class="text-center">
            <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-blue-400"></div>
            <p class="mt-4 text-gray-300 text-lg">กำลังโหลดข้อมูล...</p>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="p-8">
          <div class="bg-red-500/20 border border-red-500 rounded-2xl p-6">
            <div class="flex items-center gap-3">
              <svg class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p class="text-red-400 font-semibold">{{ error }}</p>
            </div>
          </div>
        </div>

        <!-- Content -->
        <div v-else class="relative z-10 max-h-[85vh] overflow-y-auto">
          <!-- Header Card -->
          <div class="p-8 pb-6">
            <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 shadow-2xl">
              <div class="flex items-start justify-between">
                <div class="flex items-center gap-6">
                  <!-- Avatar -->
                  <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" fill="none" viewBox="0 0 24 24">
                      <defs>
                        <linearGradient id="bluePurple" x1="0%" y1="0%" x2="100%" y2="100%">
                          <stop offset="0%" stop-color="#1e3a8a"/>
                          <stop offset="100%" stop-color="#6d28d9"/>
                        </linearGradient>
                      </defs>
                      <path
                        stroke="url(#bluePurple)"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="3"
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                  </div>
                  
                  <!-- Student Info -->
                  <div class="text-white">
                    <h1 class="text-3xl font-bold mb-2">
                      {{ student.nickname || student.firstName }}
                    </h1>
                    <p class="text-lg text-blue-100">
                      {{ student.firstName }} {{ student.lastName || '' }}
                    </p>
                    <div class="flex items-center gap-3 mt-3">
                      <span class="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-sm font-semibold">
                        {{ student.gradeName }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Edit Button -->
                <button 
                  @click="handleEdit"
                  class="px-5 py-3 bg-white text-blue-600 rounded-xl hover:bg-blue-50 transition-all shadow-lg hover:shadow-xl font-semibold flex items-center gap-2"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                  </svg>
                  แก้ไขข้อมูล
                </button>
              </div>
            </div>
          </div>

          <!-- Student Details Grid -->
          <div class="px-8 pb-6">
            <div class="bg-slate-800/40 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50">
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <StudentInfoCard icon="school" label="โรงเรียน" :value="student.schoolName || '-'" />
                <StudentInfoCard icon="book" label="แผนการเรียน" :value="student.studyPlan || '-'" />
                <StudentInfoCard icon="phone" label="เบอร์นักเรียน" :value="student.phoneStudent || '-'" />
                <StudentInfoCard icon="phone" label="เบอร์ผู้ปกครอง" :value="student.phoneParent || '-'" />
                <StudentInfoCard icon="code" label="รหัสนักเรียน" :value="student.studentCode || '-'" />
                <StudentInfoCard icon="calendar" label="วันที่เพิ่ม" :value="formatDate(student.createdAt)" />
              </div>
            </div>
          </div>

          <!-- Classes Section -->
          <div class="px-8 pb-8">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-2xl font-bold text-white flex items-center gap-3">
                <svg class="w-7 h-7 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
                คลาสเรียนทั้งหมด
              </h2>
              <div class="text-sm text-gray-300">
                <span class="font-semibold text-blue-400">{{ activeClassCount }}</span> คลาสที่เรียนอยู่
              </div>
            </div>

            <!-- Monthly Classes -->
            <div v-if="monthlyClasses.length > 0" class="mb-6">
              <h3 class="text-lg font-bold text-gray-200 mb-4 flex items-center gap-2">
                <div class="w-2 h-6 bg-gradient-to-b from-purple-500 to-purple-600 rounded-full"></div>
                รายเดือน
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard 
                  v-for="cls in monthlyClasses" 
                  :key="'monthly-' + cls.id"
                  :class-data="cls"
                  type="monthly"
                  @toggle="handleToggleClick"
                  :read-only="false"
                />
              </div>
            </div>

            <!-- Hourly Group Classes -->
            <div v-if="hourlyGroupClasses.length > 0" class="mb-6">
              <h3 class="text-lg font-bold text-gray-200 mb-4 flex items-center gap-2">
                <div class="w-2 h-6 bg-gradient-to-b from-blue-500 to-blue-600 rounded-full"></div>
                กลุ่มรวม
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard 
                  v-for="cls in hourlyGroupClasses" 
                  :key="'hourly-group-' + cls.id"
                  :class-data="cls"
                  type="hourly-group"
                  @toggle="handleToggleClick"
                  :read-only="false"
                />
              </div>
            </div>

            <!-- Hourly Individual Classes -->
            <div v-if="hourlyIndividualClasses.length > 0" class="mb-6">
              <h3 class="text-lg font-bold text-gray-200 mb-4 flex items-center gap-2">
                <div class="w-2 h-6 bg-gradient-to-b from-green-500 to-green-600 rounded-full"></div>
                PV-เดี่ยว และ PV-กลุ่ม
              </h3>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard 
                  v-for="cls in hourlyIndividualClasses" 
                  :key="'hourly-individual-' + cls.id"
                  :class-data="cls"
                  type="hourly-individual"
                  @toggle="handleToggleClick"
                  :read-only="false"
                />
              </div>
            </div>

            <!-- No Classes -->
            <div v-if="!hasAnyClasses" class="bg-slate-800/30 backdrop-blur-sm rounded-2xl p-12 text-center border border-slate-700/50">
              <svg class="w-16 h-16 text-gray-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
              </svg>
              <p class="text-gray-400 text-lg">ยังไม่มีคลาสเรียน</p>
              <p class="text-gray-500 text-sm mt-2">คลิกปุ่ม "แก้ไขข้อมูล" เพื่อแก้ไขข้อมูลพื้นฐานของนักเรียน</p>
            </div>
          </div>
        </div>

        <!-- Success Message -->
        <div v-if="successMessage" class="absolute bottom-6 right-6 bg-green-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 animate-bounce z-20">
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
          </svg>
          <span class="font-semibold">{{ successMessage }}</span>
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog -->
    <ConfirmDialog
      :show="showConfirmDialog"
      :title="confirmDialogTitle"
      :message="confirmDialogMessage"
      :sub-message="confirmDialogSubMessage"
      :confirm-text="confirmDialogConfirmText"
      :variant="confirmDialogVariant"
      :show-reason-input="true"  
      :reason-options="confirmDialogReasonOptions"  
      @confirm="confirmToggle"
      @cancel="cancelToggle"
      ref="confirmDialogRef"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import StudentClassCard from '@/components/StudentClassCard.vue';
import StudentInfoCard from '@/components/StudentInfoCard.vue';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import api from '@/api.js';

const props = defineProps({
  show: Boolean,
  studentId: Number
});

const emit = defineEmits(['close', 'edit']);

const isLoading = ref(false);
const error = ref('');
const student = ref({});
const monthlyClasses = ref([]);
const hourlyGroupClasses = ref([]);
const hourlyIndividualClasses = ref([]);
const successMessage = ref('');

// Confirmation Dialog State
const showConfirmDialog = ref(false);
const pendingToggle = ref(null);
const confirmDialogRef = ref(null);

const confirmDialogTitle = computed(() => {
  if (!pendingToggle.value) return '';
  return pendingToggle.value.currentStatus ? 'ปิดการใช้งานคลาส?' : 'เปิดการใช้งานคลาส?';
});

const confirmDialogMessage = computed(() => {
  if (!pendingToggle.value) return '';
  const className = getClassNameById(pendingToggle.value.type, pendingToggle.value.classId);
  return `คุณต้องการ${pendingToggle.value.currentStatus ? 'ปิด' : 'เปิด'}การใช้งานคลาส "${className}" ใช่หรือไม่?`;
});

const confirmDialogSubMessage = computed(() => {
  if (!pendingToggle.value) return '';
  return pendingToggle.value.currentStatus 
    ? 'กรุณาระบุหมายเหตุในการปิดการใช้งานคลาสนี้' 
    : 'กรุณาระบุหมายเหตุในการเปิดการใช้งานคลาสนี้';
});

const confirmDialogReasonOptions = computed(() => {
  if (!pendingToggle.value) return [];
  
  // ถ้ากำลังปิดการใช้งาน (currentStatus = true)
  if (pendingToggle.value.currentStatus) {
    return [
      'น้องขอจบคอร์ส',
      'น้องขอหยุดเรียน',
      'แอดมินนำน้องเข้าผิดคลาส',
      'อื่น ๆ (แจ้งหัวหน้า)'
    ];
  }
  
  // ถ้ากำลังเปิดการใช้งาน (currentStatus = false)
  return [
    'น้องกลับมาเรียน',
    'อื่น ๆ (แจ้งหัวหน้า)'
  ];
});

const confirmDialogConfirmText = computed(() => {
  if (!pendingToggle.value) return 'ยืนยัน';
  return pendingToggle.value.currentStatus ? 'ยืนยัน' : 'ยืนยัน';
});

const confirmDialogVariant = computed(() => {
  if (!pendingToggle.value) return 'warning';
  return pendingToggle.value.currentStatus ? 'warning' : 'info';
});

const getClassNameById = (type, classId) => {
  let classList = [];
  if (type === 'monthly') classList = monthlyClasses.value;
  else if (type === 'hourly-group') classList = hourlyGroupClasses.value;
  else if (type === 'hourly-individual') classList = hourlyIndividualClasses.value;
  
  const cls = classList.find(c => c.id === classId);
  return cls?.className || 'คลาสนี้';
};

const handleEdit = () => {
  const studentWithClasses = {
    ...student.value,
    classes: [
      ...monthlyClasses.value.map(cls => ({
        id: cls.id,
        subjectId: cls.subjectId,
        classTypeId: 1,
        classMode: 'GROUP',
        classSubtypeId: getSubtypeIdFromName(cls.subtypeName, 'monthly'),
        hoursTarget: null
      })),
      ...hourlyGroupClasses.value.map(cls => ({
        id: cls.id,
        subjectId: cls.subjectId,
        classTypeId: 2,
        classMode: 'GROUP',
        classSubtypeId: getSubtypeIdFromName(cls.subtypeName, 'hourly-group'),
        hoursTarget: cls.hoursTarget
      })),
      ...hourlyIndividualClasses.value.map(cls => ({
        id: cls.id,
        subjectId: cls.subjectId,
        classTypeId: 2,
        classMode: 'SINGLE',
        classSubtypeId: null,
        hoursTarget: cls.hoursTarget
      }))
    ]
  };
  
  emit('edit', studentWithClasses);
};

const getSubtypeIdFromName = (name, type) => {
  if (type === 'monthly') {
    const map = { 'จันทร์-ศุกร์': 1, 'อังคาร-พฤหัส': 2, 'เสาร์': 3, 'อาทิตย์': 4 };
    return map[name] || null;
  } else {
    const map = { 'A': 1, 'B': 2, 'C': 3, 'PV': 4 };
    return map[name] || null;
  }
};

const hasAnyClasses = computed(() => {
  return monthlyClasses.value.length > 0 || 
         hourlyGroupClasses.value.length > 0 || 
         hourlyIndividualClasses.value.length > 0;
});

const activeClassCount = computed(() => {
  return [...monthlyClasses.value, ...hourlyGroupClasses.value, ...hourlyIndividualClasses.value]
    .filter(cls => cls.isActive).length;
});

const loadStudentDetails = async () => {
  if (!props.studentId) return;
  
  isLoading.value = true;
  error.value = '';
  
  try {
    const [studentRes, classesRes] = await Promise.all([
      api.get(`/students/${props.studentId}`),
      api.get(`/students/${props.studentId}/with-classes`)
    ]);

    student.value = studentRes.data;
    
    const classesData = classesRes.data;
    const coursePurchases = classesData.coursePurchases || [];
    
    // Merge ข้อมูลจาก coursePurchases เข้าไปใน classes
    monthlyClasses.value = classesData.monthlyEnrollments || [];
    
    hourlyGroupClasses.value = (classesData.hourlyGroupEnrollments || []).map(cls => {
      // หา course purchase ที่ตรงกับวิชานี้
      const purchase = coursePurchases.find(cp => 
        cp.subjectId === cls.subjectId && 
        (cp.classType === 'hourly_group' || cp.classType === 'GROUP')
      );
      
      return {
        ...cls,
        hoursTarget: purchase?.hoursPurchased || cls.hoursTarget,
        hoursCompleted: purchase?.hoursCompleted || cls.hoursCompleted,
        completionPercentage: purchase 
          ? Math.round((purchase.hoursCompleted / purchase.hoursPurchased) * 100) 
          : cls.completionPercentage
      };
    });
    
    hourlyIndividualClasses.value = (classesData.hourlyIndividualClasses || []).map(cls => {
      // แยกเช็คตาม totalStudents: 1 คน = PV-เดี่ยว, 2+ คน = PV-กลุ่ม
      const isSingleStudent = cls.totalStudents === 1;
      const targetClassTypes = isSingleStudent 
        ? ['hourly_individual', 'INDIVIDUAL', 'individual'] 
        : ['hourly_individual_group', 'INDIVIDUAL_GROUP', 'individual_group'];
      
      const purchase = coursePurchases.find(cp => 
        cp.subjectId === cls.subjectId && 
        targetClassTypes.includes(cp.classType)
      );
      
      return {
        ...cls,
        hoursTarget: purchase?.hoursPurchased || cls.hoursTarget || 0,
        hoursCompleted: purchase?.hoursCompleted || cls.hoursCompleted || 0,
        completionPercentage: purchase 
          ? Math.round((purchase.hoursCompleted / purchase.hoursPurchased) * 100) 
          : cls.completionPercentage || 0
      };
    });
    
  } catch (err) {
    console.error('Error loading student details:', err);
    error.value = 'ไม่สามารถโหลดข้อมูลนักเรียนได้';
  } finally {
    isLoading.value = false;
  }
};

const handleToggleClick = (type, classId, currentStatus, enrollmentId) => {
  pendingToggle.value = { 
    type, 
    classId, 
    currentStatus,
    enrollmentId: enrollmentId || classId
  };
  showConfirmDialog.value = true;
};

const confirmToggle = async (reason) => {
  if (!pendingToggle.value) {
    confirmDialogRef.value?.resetLoading();
    return;
  }
  const { type, classId, currentStatus } = pendingToggle.value;
  
  try {
    let endpoint = '';
    
    if (type === 'monthly') {
      endpoint = `/enrollments/monthly/${classId}/${currentStatus ? 'deactivate' : 'activate'}`;
    } else if (type === 'hourly-group') {
      endpoint = `/enrollments/hourly-group/${classId}/${currentStatus ? 'deactivate' : 'activate'}`;
    } else if (type === 'hourly-individual') {
      endpoint = `/enrollments/hourly-individual/${pendingToggle.value.enrollmentId}/${currentStatus ? 'deactivate' : 'activate'}`;
    }
    
    // ส่ง reason ไปใน body
    await api.patch(endpoint, { reason: reason || null });
    
    successMessage.value = currentStatus ? 'ปิดการใช้งานคลาสแล้ว' : 'เปิดการใช้งานคลาสแล้ว';
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
    
    await loadStudentDetails();
    confirmDialogRef.value?.resetLoading();
    
  } catch (err) {
    console.error('Error toggling class status:', err);
    error.value = 'ไม่สามารถเปลี่ยนสถานะคลาสได้';
    setTimeout(() => {
      error.value = '';
    }, 3000);
    confirmDialogRef.value?.resetLoading();
  } finally {
    showConfirmDialog.value = false;
    pendingToggle.value = null;
  }
};

const cancelToggle = () => {
  showConfirmDialog.value = false;
  pendingToggle.value = null;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('th-TH', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const handleBackdropClick = (e) => {
  if (e.target === e.currentTarget) {
    emit('close');
  }
};

watch(() => props.show, async (newVal) => {
  if (newVal) {
    await loadStudentDetails();
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});

watch(() => props.studentId, async (newVal, oldVal) => {
  if (newVal && newVal !== oldVal && props.show) {
    await loadStudentDetails();
  }
});
</script>

<style scoped>
@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-bounce {
  animation: bounce 1s ease-in-out;
}

.animate-pulse {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.delay-1000 {
  animation-delay: 1s;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
}

/* Custom Scrollbar */
.overflow-y-auto::-webkit-scrollbar {
  width: 8px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: rgba(59, 130, 246, 0.5);
  border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: rgba(59, 130, 246, 0.7);
}
</style>