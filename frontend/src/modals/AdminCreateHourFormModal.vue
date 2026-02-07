<template>
  <div v-if="show" 
    class="fixed inset-0 backdrop-blur-sm bg-black/60 flex items-center justify-center z-50 p-4"
    @click.self="$emit('close')"
  >
    <div class="bg-gradient-to-br bg-white rounded-3xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden">
      
      <!-- Header -->
      <div class="bg-gradient-to-r from-yellow-500 to-yellow-700 p-6 rounded-t-3xl py-8">
        <div class="relative flex items-center">
          <!-- Title (center) -->
          <h1 class="absolute left-1/2 -translate-x-1/2 text-3xl font-bold text-white text-center">
            เพิ่มฟอร์มคลาสรายชั่วโมง         
            <p class="text-xl font-medium text-gray-200">
             น้อง{{ studentName }}
            </p>
          </h1>


          <!-- Close button -->
          <button 
            @click="$emit('close')" 
            class="ml-auto text-white hover:bg-white/20 rounded-full p-2 transition group"
          >
            <svg class="w-6 h-6 group-hover:rotate-90 transition-transform"
              fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoadingClasses" class="flex items-center justify-center py-24">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-yellow-400"></div>
        <p class="ml-4 text-gray-600">กำลังโหลดข้อมูล...</p>
      </div>

      <!-- Form -->
      <div v-else class="overflow-y-auto max-h-[calc(90vh-120px)] p-8">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          
          <!-- คลาสที่สอน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              คลาสที่สอน <span class="text-red-500">*</span>
            </label>
            <select
              v-model="formData.selectedClass"
              @change="handleClassChange"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
              :class="{ 'border-red-500': errors.selectedClass }"
            >
              <option value="">-- เลือกคลาสที่สอน --</option>
              <option
                v-for="cls in studentClasses"
                :key="cls.classId + '-' + cls.classType"
                :value="JSON.stringify({ classId: cls.classId, classType: cls.classType, subjectId: cls.subjectId })"
              >
                {{ cls.className }}
              </option>
            </select>
            <p v-if="errors.selectedClass" class="text-red-500 text-sm mt-1">{{ errors.selectedClass }}</p>
          </div>

          <!-- ครูผู้สอน -->
          <div class="relative">
            <label class="block text-gray-700 font-semibold mb-2">
              ครูผู้สอน <span class="text-red-500">*</span>
            </label>
            
            <!-- แสดงครูที่เลือกแล้ว -->
            <div v-if="selectedTutorName" class="flex items-center gap-2 w-full px-4 py-3 border border-green-400 bg-green-50 rounded-lg">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <span class="flex-1 text-gray-800 font-medium">{{ selectedTutorName }}</span>
              <button 
                type="button"
                @click="clearTutorSelection"
                class="text-red-500 hover:text-red-700 hover:bg-red-100 rounded-full p-1 transition"
                title="ลบการเลือก"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- ช่องค้นหาครู -->
            <div v-else>
              <input
                v-model="searchTutor"
                @focus="showTutorDropdown = true"
                @blur="handleBlurTutorSearch"
                type="text"
                placeholder="พิมพ์ชื่อครูเพื่อค้นหา"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
                :class="{ 'border-red-500': errors.tutorId }"
              />
              
              <!-- Dropdown รายชื่อครู -->
              <div 
                v-if="showTutorDropdown && filteredTutors.length > 0"
                class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto"
              >
                <button
                  v-for="tutor in filteredTutors"
                  :key="tutor.id"
                  type="button"
                  @click="selectTutor(tutor)"
                  class="w-full text-left px-4 py-3 hover:bg-yellow-100 transition-colors border-b border-gray-100 last:border-b-0"
                >
                  <span class="font-medium text-gray-800">{{ tutor.nickname || tutor.username }}</span>
                </button>
              </div>

              <!-- ไม่พบครู -->
              <div 
                v-if="showTutorDropdown && searchTutor && filteredTutors.length === 0"
                class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg px-4 py-3"
              >
                <p class="text-gray-500 text-center">ไม่พบครูที่ตรงกับ "{{ searchTutor }}"</p>
              </div>
            </div>

            <p v-if="errors.tutorId" class="text-red-500 text-sm mt-1">{{ errors.tutorId }}</p>
          </div>

          <!-- วิชา (Auto-filled from class selection) -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">วิชา</label>
            <input
              type="text"
              :value="selectedSubjectName"
              disabled
              class="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-100 text-gray-600"
              placeholder="เลือกคลาสก่อนเพื่อกำหนดวิชา"
            />
          </div>

          <!-- เนื้อหาที่สอน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              เนื้อหาที่สอน <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="formData.content"
              @blur="clearError('content')"
              required
              rows="4"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
              :class="{ 'border-red-500': errors.content }"
              placeholder="กรอกเนื้อหาที่สอน"
            ></textarea>
            <p v-if="errors.content" class="text-red-500 text-sm mt-1">{{ errors.content }}</p>
          </div>

          <!-- วันที่สอน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              วันที่สอน <span class="text-red-500">*</span>
            </label>
            <input
              v-model="formData.teachingDate"
              @blur="clearError('teachingDate')"
              type="date"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
              :class="{ 'border-red-500': errors.teachingDate }"
            />
            <p v-if="errors.teachingDate" class="text-red-500 text-sm mt-1">{{ errors.teachingDate }}</p>
          </div>

          <!-- จำนวนชั่วโมงที่สอน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              จำนวนชั่วโมงที่สอน <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.hoursTaught"
              @blur="clearError('hoursTaught')"
              type="number"
              step="0.5"
              min="0"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
              :class="{ 'border-red-500': errors.hoursTaught }"
              placeholder="เช่น 2"
            />
            <p v-if="errors.hoursTaught" class="text-red-500 text-sm mt-1">{{ errors.hoursTaught }}</p>
          </div>

          <!-- หมายเหตุ -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">หมายเหตุ <span class="text-red-500">*</span></label>
            <select
              v-model="formData.remark"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
            >
              <option value="">-- เลือกหมายเหตุ -- </option>
              <option v-for="opt in remarkOptions" :key="opt" :value="opt">
                {{ opt }}
              </option>
            </select>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-center space-x-4 pt-4">
            <button
              type="button"
              @click="$emit('close')"
              class="px-8 py-3 bg-gray-300 text-gray-800 font-semibold rounded-lg hover:bg-gray-400 transition"
            >
              ยกเลิก
            </button>
            <button
              type="submit"
              :disabled="!isFormValid || isSubmitting"
              class="px-8 py-3 bg-yellow-600 text-white font-semibold rounded-lg hover:bg-yellow-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? 'กำลังบันทึก...' : 'ยืนยัน' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Success/Error Message -->
      <div v-if="successMessage" class="absolute bottom-6 left-1/2 transform -translate-x-1/2 bg-green-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 animate-bounce z-20">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <span class="font-semibold">{{ successMessage }}</span>
      </div>

      <div v-if="errorMessage" class="absolute bottom-6 left-1/2 transform -translate-x-1/2 bg-red-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 z-20">
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="font-semibold">{{ errorMessage }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import api from '@/api.js';

const props = defineProps({
  show: Boolean,
  studentId: {
    type: Number,
    required: true
  },
  studentName: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['close', 'success']);

const isLoadingClasses = ref(false);
const isSubmitting = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

const studentClasses = ref([]);
const tutors = ref([]);
const searchTutor = ref('');
const showTutorDropdown = ref(false);
const selectedTutorName = ref('');

const filteredTutors = computed(() => {
  if (!searchTutor.value.trim()) return tutors.value;
  
  const keyword = searchTutor.value.toLowerCase().trim();
  return tutors.value.filter(tutor => {
    const name = (tutor.nickname || tutor.username || '').toLowerCase();
    return name.includes(keyword);
  });
});

const selectTutor = (tutor) => {
  formData.value.tutorId = tutor.id;
  selectedTutorName.value = tutor.nickname || tutor.username;
  searchTutor.value = '';
  showTutorDropdown.value = false;
  clearError('tutorId');
};

const clearTutorSelection = () => {
  formData.value.tutorId = '';
  selectedTutorName.value = '';
  searchTutor.value = '';
};

const handleBlurTutorSearch = () => {
  window.setTimeout(() => {
    showTutorDropdown.value = false;
  }, 200);
};

const formData = ref({
  selectedClass: '',
  classId: null,
  classType: '',
  subjectId: null,
  tutorId: '',
  content: '',
  teachingDate: '',
  hoursTaught: null,
  remark: ''
});

const errors = ref({
  selectedClass: '',
  tutorId: '',
  content: '',
  teachingDate: '',
  hoursTaught: ''
});

const remarkOptions = [
  'ครูกรอกเกิน',
  'แอดมินนำเด็กเข้าคลาสไม่ทัน',
  'แอดมินนำเด็กออกจากคลาสไม่ทัน',
  'แก้วันเรียน',
  'น้องขอจบคอร์ส',
  'อื่น ๆ แจ้งหัวหน้า'
];

const selectedSubjectName = computed(() => {
  if (!formData.value.subjectId) return '';
  const selectedClass = studentClasses.value.find(
    cls => cls.subjectId === formData.value.subjectId
  );
  return selectedClass ? selectedClass.subjectName : '';
});

const isFormValid = computed(() => {
  return (
    formData.value.selectedClass !== '' &&
    formData.value.tutorId !== '' &&
    formData.value.content.trim() !== '' &&
    formData.value.teachingDate !== '' &&
    formData.value.hoursTaught !== null &&
    formData.value.hoursTaught > 0 &&
    formData.value.remark !== '' &&
    Object.values(errors.value).every(e => e === '')
  );
});

watch(() => props.show, async (newVal) => {
  if (newVal) {
    await loadData();
  } else {
    resetForm();
  }
});

const loadData = async () => {
  isLoadingClasses.value = true;
  
  const token = sessionStorage.getItem('token') || localStorage.getItem('token');
  
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error('Token parse error:', e);
    }
  }
  
  try {
    const classesResponse = await api.get(`/hour-forms/student/${props.studentId}/classes`);
    studentClasses.value = classesResponse.data;

    const tutorsResponse = await api.get('/hour-forms/tutors');
    tutors.value = tutorsResponse.data;

  } catch (error) {
    console.error('=== LOAD DATA ERROR ===');
    console.error('Error details:', error);
    console.error('Response status:', error.response?.status);
    console.error('Response data:', error.response?.data);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลได้';
    setTimeout(() => errorMessage.value = '', 3000);
  } finally {
    isLoadingClasses.value = false;
  }
};

const handleClassChange = () => {
  if (!formData.value.selectedClass) {
    formData.value.classId = null;
    formData.value.classType = '';
    formData.value.subjectId = null;
    return;
  }

  try {
    const classData = JSON.parse(formData.value.selectedClass);
    formData.value.classId = classData.classId;
    formData.value.classType = classData.classType;
    formData.value.subjectId = classData.subjectId;
    clearError('selectedClass');
  } catch (error) {
    console.error('Error parsing class data:', error);
    errors.value.selectedClass = 'เกิดข้อผิดพลาดในการเลือกคลาส';
  }
};

const clearError = (fieldName) => {
  const value = formData.value[fieldName];
  if (value && !(typeof value === 'string' && value.trim() === '')) {
    errors.value[fieldName] = '';
  }
};

const handleSubmit = async () => {
  if (!isFormValid.value) return;

  isSubmitting.value = true;
  errorMessage.value = '';

  try {
    const dataToSubmit = {
      classId: formData.value.classId,
      classType: formData.value.classType,
      subjectId: formData.value.subjectId,
      tutorId: formData.value.tutorId,
      content: formData.value.content.trim(),
      teachingDate: formData.value.teachingDate,
      hoursTaught: formData.value.hoursTaught,
      remark: formData.value.remark ? formData.value.remark.trim() : ''
    };

    const response = await api.post(
      `/hour-forms/admin/for-student?studentId=${props.studentId}`,
      dataToSubmit
    );

    if (response.status === 201) {
      emit('success');
    }
  } catch (error) {
    console.error('Error submitting form:', error);
    errorMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการบันทึกข้อมูล';
    setTimeout(() => errorMessage.value = '', 5000);
  } finally {
    isSubmitting.value = false;
  }
};

const resetForm = () => {
  formData.value = {
    selectedClass: '',
    classId: null,
    classType: '',
    subjectId: null,
    tutorId: '',
    content: '',
    hoursTaught: null,
    remark: ''
  };
  
  errors.value = {
    selectedClass: '',
    tutorId: '',
    content: '',
    teachingDate: '',
    hoursTaught: ''
  };

  searchTutor.value = '';
  showTutorDropdown.value = false;
  selectedTutorName.value = '';
};

const getTodayDate = () => {
  const today = new Date();
  return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
};
</script>

<style scoped>
input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
  filter: invert(50%);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0) translateX(-50%);
  }
  50% {
    transform: translateY(-10px) translateX(-50%);
  }
}

.animate-bounce {
  animation: bounce 0.5s ease-in-out 2;
}
</style>