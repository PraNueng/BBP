<template>
  <div v-if="show" 
    class="fixed inset-0 backdrop-blur-sm bg-black/60 flex items-center justify-center z-50 p-4"
    @click.self="$emit('close')"
  >
    <div class="bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-3xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-hidden relative">
      
      <!-- Animated Background -->
      <div class="absolute inset-0 overflow-hidden rounded-3xl pointer-events-none">
        <div class="absolute w-64 h-64 bg-blue-500/20 rounded-full blur-3xl -top-32 -left-32 animate-pulse"></div>
        <div class="absolute w-64 h-64 bg-indigo-500/20 rounded-full blur-3xl -bottom-32 -right-32 animate-pulse delay-1000"></div>
      </div>

      <!-- Header -->
      <div class="relative z-10 bg-gradient-to-r from-blue-600 to-indigo-700 p-6 rounded-t-3xl shadow-lg">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="p-2 bg-white/20 rounded-lg">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="isClassSelectorOnly" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </div>
            <!-- Dynamic Title based on mode -->
            <h2 class="text-2xl font-bold text-white">
              {{ isClassSelectorOnly ? 'ลงคอร์สให้นักเรียน' : 'แก้ไขข้อมูลนักเรียน' }}
            </h2>
          </div>
          <button 
            @click="$emit('close')" 
            class="text-white hover:bg-white/20 rounded-full p-2 transition group"
          >
            <svg class="w-6 h-6 group-hover:rotate-90 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <!-- Show student name -->
        <p v-if="formData.firstName" class="text-purple-100 mt-2">
          {{ formData.nickname || formData.firstName }} {{ formData.lastName || '' }}
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoadingData" class="relative z-10 flex items-center justify-center py-24">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-blue-400"></div>
          <p class="mt-4 text-gray-300 text-lg">กำลังโหลดข้อมูล...</p>
        </div>
      </div>

      <!-- Form -->
      <div v-else class="relative z-10 overflow-y-auto max-h-[calc(90vh-180px)] p-6">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          
          <!-- ข้อมูลพื้นฐาน - ซ่อนเมื่อ isClassSelectorOnly = true -->
          <div v-if="!isClassSelectorOnly" class="bg-white backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50">
            <h3 class="text-xl font-bold text-blue-600 mb-4 flex items-center gap-2">
              <div class="w-2 h-6 bg-gradient-to-b from-blue-500 to-blue-600 rounded-full"></div>
              ข้อมูลพื้นฐาน
            </h3>
            <StudentFormFields 
              v-model="formData" 
              :show-class-selector="false" 
              :disable-grade-change="hasExistingClasses"
            />
          </div>

          <!-- คอร์สเรียน - แสดงเฉพาะเมื่อ isClassSelectorOnly = true -->
          <div v-if="isClassSelectorOnly" class="bg-white backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50">
            <h3 class="text-xl font-bold text-purple-600 mb-4 flex items-center gap-2">
              <div class="w-2 h-6 bg-gradient-to-b from-purple-500 to-purple-600 rounded-full"></div>
              เลือกคอร์สที่ต้องการ
            </h3>

            <StudentCoursePurchaseSelector 
              v-model="formData.coursePurchases" 
              :read-only-mode="true"
              :showHistoryButton="false"
              @validate="validateCoursePurchases" 
              @duplicate-error="handleDuplicateError"
            />

            <!-- ประวัติการซื้อคอร์ส (Inline) -->
            <div v-if="formData.id" class="mt-8">
              <h3 class="text-xl font-bold text-blue-600 mb-4 flex items-center gap-2">
                <div class="w-2 h-6 bg-gradient-to-b from-blue-500 to-indigo-600 rounded-full"></div>
                ประวัติการซื้อคอร์ส
              </h3>

              <!-- Loading State -->
              <div v-if="loadingHistory" class="flex items-center justify-center py-12">
                <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-4 border-blue-400"></div>
                <p class="ml-4 text-gray-600">กำลังโหลดประวัติ...</p>
              </div>

              <!-- Empty State -->
              <div v-else-if="purchaseHistory.length === 0" class="text-center py-8">
                <p class="text-gray-500 text-base">ยังไม่มีประวัติการซื้อคอร์ส</p>
              </div>

              <!-- History List -->
              <div v-else class="space-y-3 max-h-96 overflow-y-auto pr-2">
                <div v-for="(item, index) in purchaseHistory" :key="index" 
                  class="p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl border-2 border-blue-200 shadow-sm hover:shadow-md transition-all">
                  
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-2">
                        <span v-if="item.action === 'INSERT'" class="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full">เพิ่ม</span>
                        <span v-else-if="item.action === 'UPDATE'" class="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-bold rounded-full">แก้ไข</span>
                        <span v-else-if="item.action === 'DELETE'" class="px-3 py-1 bg-red-100 text-red-700 text-xs font-bold rounded-full">ลบ</span>
                        <span class="text-sm text-gray-600">{{ formatFieldName(item.fieldName) }}</span>
                      </div>

                      <div v-if="item.oldValue" class="mb-1">
                        <span class="text-xs text-gray-500">จาก:</span>
                        <span class="text-sm text-red-600 font-medium ml-2">{{ item.oldValue }}</span>
                      </div>

                      <div v-if="item.newValue">
                        <span class="text-xs text-gray-500">เป็น:</span>
                        <span class="text-sm text-green-600 font-medium ml-2">{{ item.newValue }}</span>
                      </div>
                    </div>

                    <div class="text-right">
                      <p class="text-xs text-gray-500">{{ formatHistoryDate(item.updatedAt) }}</p>
                      <p class="text-xs text-gray-600 font-medium">โดย: {{ item.updatedBy }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-4 justify-end pt-4">
            <button 
              @click="$emit('close')" 
              type="button" 
              class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
            >
              ยกเลิก
            </button>
            <button 
              type="submit" 
              :disabled="isSubmitting || isSaveDisabled"
              class="px-6 py-3 font-semibold rounded-xl text-white transition-all shadow-lg transform hover:scale-105 disabled:transform-none disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              :class="isSaveDisabled ? 'bg-gray-600' : 'bg-gradient-to-r from-green-600 to-green-700 hover:from-blue-700 hover:to-indigo-800'"
            >
              <!-- <svg v-if="!isSubmitting" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <svg v-else class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg> -->
              {{ isSubmitting ? 'กำลังบันทึก...' : 'ยืนยัน' }}
            </button>
          </div>
        </form>
      </div>

      <!-- <CoursePurchaseHistoryModal
        :show="showPurchaseHistory"
        :studentId="formData.id"
        @close="showPurchaseHistory = false"
      /> -->

      <!-- Error Message -->
      <div v-if="errorMessage" class="scale-90 absolute bottom-6 bg-red-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 animate-none z-20">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="font-semibold">{{ errorMessage }}</span>
        <span v-if="duplicateCourseError" class="mt-2 text-sm text-red-600 font-semibold">
          {{ duplicateCourseError }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import StudentFormFields from './StudentFormFields.vue';
import StudentCoursePurchaseSelector from './StudentCoursePurchaseSelector.vue';
import CoursePurchaseHistoryModal from '@/modals/CoursePurchaseHistoryModal.vue';
import api from '@/api.js';

const props = defineProps({
  show: Boolean,
  student: Object,
  isSubmitting: Boolean,
  // prop สำหรับควบคุมการแสดง ClassSelector
  classifierOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'success']);

const isLoadingData = ref(false);
const originalData = ref(null);
const errorMessage = ref('');
const duplicateCourseError = ref('');
const subjects = ref([]);
const showPurchaseHistory = ref(false);
const purchaseHistory = ref([]);
const loadingHistory = ref(false);

const formatFieldName = (fieldName) => {
  if (fieldName === 'coursePurchaseAdded') {
    return 'เพิ่มคอร์ส';
  }
  return fieldName;
};

const loadSubjects = async () => {
  try {
    const response = await api.get('/subjects/all');
    subjects.value = response.data.filter(item => item.isActive);
  } catch (error) {
    console.error('Error loading subjects:', error);
  }
};

const loadPurchaseHistory = async () => {
  if (!formData.value.id) return;
  
  loadingHistory.value = true;
  try {
    const response = await api.get(`/students/${formData.value.id}/history`);
    
    // กรองเฉพาะประวัติที่เกี่ยวกับคอร์ส
    purchaseHistory.value = response.data.filter(item => {
      const courseFields = ['coursePurchaseAdded', 'แก้ไขคอร์ส', 'ลบคอร์ส'];
      return courseFields.includes(item.fieldName);
    });

  } catch (error) {
    console.error('Error loading course history:', error);
    purchaseHistory.value = [];
  } finally {
    loadingHistory.value = false;
  }
};

const formatHistoryDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('th-TH', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const formData = ref({
  id: null,
  firstName: '',
  lastName: '',
  nickname: '',
  schoolName: '',
  gradeId: null,
  phoneStudent: '',
  phoneParent: '',
  studyPlan: '',
  coursePurchases: []
});

// Computed property สำหรับ classifierOnly mode
const isClassSelectorOnly = computed(() => props.classifierOnly);

// ตรวจสอบว่ามี id นักเรียนหรือไม่
const hasExistingClasses = computed(() => {
  return originalData.value && originalData.value.id !== null;
});

const isFormChanged = computed(() => {
  if (!originalData.value) return false;
  
  // ถ้า classifierOnly mode ให้ตรวจสอบแค่ classes
  if (isClassSelectorOnly.value) {
    return JSON.stringify(formData.value.coursePurchases) !== JSON.stringify(originalData.value.coursePurchases);
  }
  
  // Mode ปกติ - เปรียบเทียบข้อมูลพื้นฐานแยกจาก classes
  const currentBasic = {
    firstName: formData.value.firstName,
    lastName: formData.value.lastName,
    nickname: formData.value.nickname,
    schoolName: formData.value.schoolName,
    gradeId: formData.value.gradeId,
    phoneStudent: formData.value.phoneStudent,
    phoneParent: formData.value.phoneParent,
    studyPlan: formData.value.studyPlan
  };
  
  const originalBasic = {
    firstName: originalData.value.firstName,
    lastName: originalData.value.lastName,
    nickname: originalData.value.nickname,
    schoolName: originalData.value.schoolName,
    gradeId: originalData.value.gradeId,
    phoneStudent: originalData.value.phoneStudent,
    phoneParent: originalData.value.phoneParent,
    studyPlan: originalData.value.studyPlan
  };
  
  // ตรวจสอบว่าข้อมูลพื้นฐานหรือ classes เปลี่ยนแปลงหรือไม่
  const basicChanged = JSON.stringify(currentBasic) !== JSON.stringify(originalBasic);
  const classesChanged = JSON.stringify(formData.value.coursePurchases) !== JSON.stringify(originalData.value.coursePurchases);
  
  return basicChanged || classesChanged;
});

const isSaveDisabled = computed(() => {
  return isFormInvalid.value || !isFormChanged.value || hasUnconfirmedNewCourses.value;
});

const isFormInvalid = computed(() => {
  const f = formData.value;
  
  // ถ้า classifierOnly mode ให้ตรวจสอบแค่ classes
  if (isClassSelectorOnly.value) {
    
    // ตรวจสอบว่าคลาสแต่ละอันมีข้อมูลครบถ้วน
    return f.coursePurchases.some(course => {
      if (!course.id) {
        if (!course.subjectId) return true;
        if (course.classType !== 'MONTHLY' && (!course.hoursPurchased || course.hoursPurchased <= 0)) return true;
        if (course.classType === 'MONTHLY' && !course.monthlySubtypeId) return true; // เพิ่ม
        if (!course.classType) return true;
      }
      return false;
    });
  }
  
  // Mode ปกติ - ตรวจสอบข้อมูลทั้งหมด
  if (!f.firstName || !f.gradeId) return true;

  if (f.coursePurchases && f.coursePurchases.length > 0) {
    return f.coursePurchases.some(course => {
      if (!course.subjectId) return true;
      if (course.classType !== 'MONTHLY' && (!course.hoursPurchased || course.hoursPurchased <= 0)) return true;
      if (course.classType === 'MONTHLY' && !course.monthlySubtypeId) return true;
      if (!course.classType) return true;
      return false;
    });
  }

  return false;
});

const hasUnconfirmedNewCourses = computed(() => {
  // หาเฉพาะคอร์สใหม่ (ไม่มี id)
  const newCoursesOnly = formData.value.coursePurchases.filter(course => !course.id);
  
  // ถ้าไม่มีคอร์สใหม่ = ไม่ติด
  if (newCoursesOnly.length === 0) return false;
  
  // ถ้ามีคอร์สใหม่แต่ยังไม่ confirm ครบ = ติด
  return newCoursesOnly.some(course => !course.confirmed);
});

// แปลงค่า classType จาก database format เป็น frontend format
const normalizeClassTypeForFrontend = (classType) => {
  if (!classType) return '';
  
  const normalized = classType.toLowerCase();
  
  if (normalized === 'hourly_group') {
    return 'GROUP';
  } else if (normalized === 'hourly_individual') {
    return 'INDIVIDUAL';
  } else if (normalized === 'hourly_individual_group') {
    return 'INDIVIDUAL_GROUP';
  }
  
  // ถ้าเป็น format ของ frontend อยู่แล้ว (GROUP, INDIVIDUAL)
  return classType.toUpperCase();
};

// โหลดข้อมูลนักเรียนพร้อมคลาส
const loadStudentData = async (studentId) => {
  if (!studentId) return;
  
  isLoadingData.value = true;
  
  try {
    const response = await api.get(`/students/${studentId}/with-classes`);
    const data = response.data;

    const coursePurchases = data.coursePurchases?.map(cp => ({
      id: cp.purchaseId,
      subjectId: cp.subjectId,
      hoursPurchased: cp.hoursPurchased,
      classType: normalizeClassTypeForFrontend(cp.classType),
      monthlySubtypeId: cp.monthlySubtypeId || null,
      editReason: cp.editReason || null,
      hasEditReason: !!cp.editReason
    })) || [];
    
    formData.value = {
      id: data.id,
      firstName: data.firstName || '',
      lastName: data.lastName || '',
      nickname: data.nickname || '',
      schoolName: data.schoolName || '',
      gradeId: data.gradeId || null,
      phoneStudent: data.phoneStudent || '',
      phoneParent: data.phoneParent || '',
      studyPlan: data.studyPlan || '',
      coursePurchases: coursePurchases
    };
    
    originalData.value = JSON.parse(JSON.stringify(formData.value));
    
  } catch (error) {
    console.error('Error loading student data:', error);
  } finally {
    isLoadingData.value = false;
  }
};

watch(() => props.student, async (newStudent) => {
  if (newStudent && newStudent.id) {
    // Reset data ก่อน
    purchaseHistory.value = [];
    formData.value = {
      id: null,
      firstName: '',
      lastName: '',
      nickname: '',
      schoolName: '',
      gradeId: null,
      phoneStudent: '',
      phoneParent: '',
      studyPlan: '',
      coursePurchases: []
    };
    
    // โหลดข้อมูลใหม่
    await loadStudentData(newStudent.id);
    
    // โหลดประวัติหลังจากโหลดข้อมูลเสร็จแล้ว
    await loadPurchaseHistory();
  }
}, { immediate: true });

const hasChanges = (current) => {
  if (!current?.id) return false;

  const original = originalData.value.coursePurchases.find(
    o => o.id === current.id
  );

  if (!original) return false;

  // ถ้ามี editReason = แสดงว่ามีการแก้ไข
  if (current.hasEditReason) {
    return true;
  }

  // เช็ค subjectId
  if (parseInt(original.subjectId) !== parseInt(current.subjectId)) {
    return true;
  }

  // เช็ค classType
  if (normalizeClassTypeForBackend(original.classType) !== normalizeClassTypeForBackend(current.classType)) {
    return true;
  }

  // เช็ค hoursPurchased
  const origHours = original.hoursPurchased;
  const currHours = current.hoursPurchased;
  
  if ((origHours == null) && (currHours == null)) {
    return false;
  }
  
  if ((origHours == null) !== (currHours == null)) {
    return true;
  }
  
  return parseFloat(origHours) !== parseFloat(currHours);
};

// แปลงค่า classType จาก frontend format กลับเป็น database format
const normalizeClassTypeForBackend = (classType) => {
  if (!classType) return null;
  return classType.toUpperCase();
};

const handleSubmit = async () => {
  if (isFormInvalid.value) return;

    // เพิ่มการตรวจสอบ duplicate ก่อน submit
  const duplicateCheck = checkForDuplicatePurchases();
  if (duplicateCheck.isDuplicate) {
    const subjectName = subjects.value.find(
      s => s.id === duplicateCheck.subjectId
    )?.subjectName || 'วิชานี้';
    
    const classTypeText = duplicateCheck.classType === 'GROUP' ? 'กลุ่ม' : 'เดี่ยว';
    
    errorMessage.value = `ไม่สามารถซื้อคอร์ส${subjectName} ประเภท${classTypeText}ซ้ำได้`;
    
    setTimeout(() => {
      errorMessage.value = '';
    }, 5000);
    return;
  }
  
  errorMessage.value = '';
  
  const existingCourses = formData.value.coursePurchases
    .filter(course => course.id)
    .map(course => ({
      id: course.id,
      subjectId: parseInt(course.subjectId),
      hoursPurchased: parseFloat(course.hoursPurchased),
      classType: normalizeClassTypeForBackend(course.classType)
    }));

  const newCourses = formData.value.coursePurchases
    .filter(course => !course.id)
    .map(course => ({
      subjectId: parseInt(course.subjectId),
      hoursPurchased: course.hoursPurchased ? parseFloat(course.hoursPurchased) : null,
      classType: normalizeClassTypeForBackend(course.classType),
      monthlySubtypeId: course.monthlySubtypeId ? parseInt(course.monthlySubtypeId) : null
    }));

  const updatedCourses = formData.value.coursePurchases
    .filter(c => c.id && hasChanges(c))
    .map(c => ({
      id: c.id,
      subjectId: parseInt(c.subjectId),
      hoursPurchased: c.hoursPurchased ? parseFloat(c.hoursPurchased) : null,
      classType: normalizeClassTypeForBackend(c.classType),
      monthlySubtypeId: c.monthlySubtypeId ? parseInt(c.monthlySubtypeId) : null,
      editReason: c.editReason || null
    }));

  const deletedIds = originalData.value.coursePurchases
    .filter(orig => !formData.value.coursePurchases.some(curr => curr.id === orig.id))
    .map(c => c.id);

  const coursePurchasesWithId = [...existingCourses, ...newCourses];

   const payload = {
    id: formData.value.id,
    firstName: formData.value.firstName,
    lastName: formData.value.lastName,
    nickname: formData.value.nickname,
    schoolName: formData.value.schoolName,
    gradeId: formData.value.gradeId,
    phoneStudent: formData.value.phoneStudent,
    phoneParent: formData.value.phoneParent,
    studyPlan: formData.value.studyPlan,
    coursePurchases: newCourses,
    updatedCoursePurchases: updatedCourses,
    deletedCoursePurchaseIds: deletedIds
  };

  try {
    await api.put(`/students/${payload.id}`, payload);
    emit('success');
    emit('close');
    showPurchaseHistory.value = false;
  } catch (error) {
    console.error('Error updating student:', error);
    
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message;
    } else if (error.response?.status === 400) {
      errorMessage.value = 'ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง';
    } else {
      errorMessage.value = 'เกิดข้อผิดพลาดในการแก้ไขข้อมูล';
    }

    setTimeout(() => {
      errorMessage.value = "";
    }, 6000)
  }
};

const checkForDuplicatePurchases = () => {
  const courseMap = new Map();
  
  for (let i = 0; i < formData.value.coursePurchases.length; i++) {
    const course = formData.value.coursePurchases[i];
    
    if (!course.subjectId || !course.classType) continue;
    
    const key = `${course.subjectId}-${course.classType}`;
    
    if (courseMap.has(key)) {
      return {
        isDuplicate: true,
        subjectId: course.subjectId,
        classType: course.classType,
        index1: courseMap.get(key),
        index2: i
      };
    }
    
    courseMap.set(key, i);
  }
  
  return { isDuplicate: false };
};

const handleDuplicateError = (duplicateInfo) => {
  const subjectName = subjects.value.find(
    s => s.id === duplicateInfo.subjectId
  )?.subjectName || 'วิชานี้';
  
  const classTypeText = duplicateInfo.classType === 'GROUP' ? 'กลุ่ม' : 'เดี่ยว';
  
  errorMessage.value = `มีคอร์ส${subjectName} ประเภท${classTypeText}ซ้ำกันในคอร์สที่ ${duplicateInfo.index1 + 1} และ ${duplicateInfo.index2 + 1}\nกรุณาแก้ไขประเภทคลาสให้ถูกต้อง`;
  
  setTimeout(() => {
    errorMessage.value = '';
  }, 5000);
};

const validateCoursePurchases = () => {
  // Validation จะถูกจัดการโดย computed property แล้ว
  return true;
};

onMounted(() => {
  loadSubjects();
});

watch(() => props.show, (newVal) => {
  if (!newVal) {
    // เมื่อ modal ปิด ให้ reset showPurchaseHistory
    showPurchaseHistory.value = false;
  }
});
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.delay-1000 {
  animation-delay: 1s;
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: rgba(59, 130, 246, 0.5);
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(59, 130, 246, 0.7);
}
</style>