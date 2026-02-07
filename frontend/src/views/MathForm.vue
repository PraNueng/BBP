<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 to-red-100">
    <NavBar />

    <div class="container mx-auto px-4 lg:px-8 py-8 max-w-4xl">
      <div class="bg-gradient-to-r from-red-400 to-red-700 rounded-t-2xl shadow-xl text-center py-10">
        <h1 class="text-xl font-semibold text-white mb-2">แบบฟอร์มบันทึกการสอน</h1>
        <h1 class="text-5xl font-extrabold text-white drop-shadow-md">คณิตศาสตร์ (รายเดือน)</h1>
      </div>
      
      <div class="bg-white rounded-b-2xl shadow-2xl p-8">
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              :class="{ 'border-red-500': errors.selectedClass }"
            >
              <option value="">-- เลือกคลาสรายเดือน --</option>
              <option
                v-for="cls in monthlyClasses"
                :key="cls.id"
                :value="JSON.stringify({ id: cls.id, subjectId: cls.subjectId, grade: cls.grade })"
              >
                {{ cls.className }}
              </option>
            </select>
            <p v-if="errors.selectedClass" class="text-red-500 text-sm mt-1">{{ errors.selectedClass }}</p>
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              :class="{ 'border-red-500': errors.hoursTaught }"
              placeholder="เช่น 2"
            />
            <p v-if="errors.hoursTaught" class="text-red-500 text-sm mt-1">{{ errors.hoursTaught }}</p>
          </div>

          <!-- จำนวนน้องที่มา -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              จำนวนน้องที่มา <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.studentsPresent"
              @blur="clearError('studentsPresent')"
              type="number"
              min="0"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              :class="{ 'border-red-500': errors.studentsPresent }"
              placeholder="กรอกจำนวนน้องที่มา"
            />
            <p v-if="errors.studentsPresent" class="text-red-500 text-sm mt-1">{{ errors.studentsPresent }}</p>
          </div>

          <!-- จำนวนน้องที่ขาดเรียน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              จำนวนน้องที่ขาดเรียน <span class="text-red-500">*</span>
            </label>
            <input
              v-model.number="formData.studentsAbsent"
              @blur="clearError('studentsAbsent')"
              type="number"
              min="0"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              :class="{ 'border-red-500': errors.studentsAbsent }"
              placeholder="หากน้องมาครบ ให้ใส่ 0"
            />
            <p v-if="errors.studentsAbsent" class="text-red-500 text-sm mt-1">{{ errors.studentsAbsent }}</p>
          </div>

          <!-- หมายเหตุ -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">หมายเหตุ</label>
            <textarea
              v-model="formData.remark"
              rows="3"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="หมายเหตุเพิ่มเติม (ถ้ามี)"
            ></textarea>
          </div>

          <!-- ข้อความแจ้งเตือน -->
          <!-- <div class="bg-blue-50 border-l-4 border-blue-500 p-4 rounded">
            <p class="text-blue-700 text-sm">
              <strong>หมายเหตุ:</strong> ฟอร์มนี้ใช้สำหรับบันทึกชั่วโมงการสอน<strong>คลาสรายเดือนเท่านั้น</strong> 
              และไม่มีผลต่อการตัดชั่วโมงคอร์สของนักเรียน (ใช้สำหรับประเมินเงินเดือนครู)
            </p>
          </div> -->

          <!-- ปุ่มส่ง -->
          <div class="flex justify-center space-x-4 pt-4">
            <button
              type="button"
              @click="handleCancel"
              class="px-8 py-3 bg-gray-300 text-gray-800 font-semibold rounded-lg hover:bg-gray-400 transition"
            >
              ยกเลิก
            </button>
            <button
              type="submit"
              :disabled="!isFormValid || isSubmitting"
              class="px-8 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? 'กำลังบันทึก...' : 'บันทึก' }}
            </button>
          </div>
        </form>

        <CreateFormSuccessModal
          :visible="showModal"
          :type="modalType"
          :message="modalMessage"
          @close="showModal = false"
          @continue="handleContinue"
          @cancel="cancelAction"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import NavBar from '@/components/NavBar.vue';
import CreateFormSuccessModal from '@/modals/CreateFormSuccessModal.vue';
import api from '@/api.js';

const router = useRouter();
const isSubmitting = ref(false);
const showModal = ref(false);
const modalType = ref('success');
const modalMessage = ref('');

const monthlyClasses = ref([]);

const formData = ref({
  selectedClass: '',
  classId: null,
  subjectId: null,
  content: '',
  teachingDate: '',
  hoursTaught: null,
  studentsPresent: null,
  studentsAbsent: null,
  remark: ''
});

const errors = ref({
  selectedClass: '',
  content: '',
  teachingDate: '',
  hoursTaught: '',
  studentsPresent: '',
  studentsAbsent: ''
});

const isFormValid = computed(() => {
  return (
    formData.value.selectedClass !== '' &&
    formData.value.content.trim() !== '' &&
    formData.value.teachingDate !== '' &&
    formData.value.hoursTaught !== null &&
    formData.value.studentsPresent !== null &&
    formData.value.studentsAbsent !== null &&
    Object.values(errors.value).every(e => e === '')
  );
});

onMounted(async () => {
  formData.value.teachingDate = getTodayDate();
  await loadMonthlyClasses();
});

const loadMonthlyClasses = async () => {
  try {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    const tutorId = getTutorIdFromToken(token);

    // ดึงเฉพาะคลาสรายเดือนที่ครูสอน
    const response = await api.get(`/monthly-classes?tutorId=${tutorId}&active=true`);
    monthlyClasses.value = response.data
      .filter(cls => cls.className.startsWith('คณิตศาสตร์'))
      .map(cls => ({
        id: cls.id,
        className: cls.className,
        subjectId: cls.subject?.id || cls.subjectId,
        subjectName: cls.subject?.subjectName || cls.subjectName || '-',
        grade: cls.grade?.gradeName || cls.grade || '-'
      }));

  } catch (error) {
    console.error('Error loading monthly classes:', error);
    modalType.value = 'error';
    modalMessage.value = 'เกิดข้อผิดพลาดในการโหลดข้อมูลคลาส';
    showModal.value = true;
  }
};

const handleClassChange = () => {
  if (!formData.value.selectedClass) {
    formData.value.classId = null;
    formData.value.subjectId = null;
    return;
  }

  try {
    const classData = JSON.parse(formData.value.selectedClass);
    formData.value.classId = classData.id;
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
  if (!formData.value.selectedClass) {
    errors.value.selectedClass = 'กรุณาเลือกคลาสที่สอน';
    return;
  }

  isSubmitting.value = true;

  try {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    const dataToSubmit = {
      classId: formData.value.classId,
      subjectId: formData.value.subjectId,
      content: formData.value.content.trim(),
      teachingDate: formData.value.teachingDate,
      hoursTaught: formData.value.hoursTaught,
      studentsPresent: formData.value.studentsPresent,
      studentsAbsent: formData.value.studentsAbsent,
      remark: formData.value.remark ? formData.value.remark.trim() : ''
    };

    const response = await api.post('/math-forms', dataToSubmit);

    if (response.status === 201) {
      modalType.value = 'success';
      modalMessage.value = 'บันทึกข้อมูลสำเร็จ! ชั่วโมงถูกบันทึกเรียบร้อยแล้ว';
      showModal.value = true;
    }
  } catch (error) {
    console.error('Error submitting form:', error);
    modalType.value = 'error';
    modalMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการบันทึกข้อมูล';
    showModal.value = true;
  } finally {
    isSubmitting.value = false;
  }
};

const resetForm = () => {
  const oldClass = formData.value.selectedClass;
  
  formData.value = {
    selectedClass: oldClass,
    classId: formData.value.classId,
    subjectId: formData.value.subjectId,
    content: '',
    teachingDate: getTodayDate(),
    hoursTaught: null,
    studentsPresent: null,
    studentsAbsent: null,
    remark: ''
  };
  
  errors.value = {
    selectedClass: '',
    content: '',
    teachingDate: '',
    hoursTaught: '',
    studentsPresent: '',
    studentsAbsent: ''
  };
};

const getTodayDate = () => {
  const today = new Date();
  return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
};

const handleCancel = () => {
  router.push('/home-tutor');
};

const handleContinue = () => {
  resetForm();
  showModal.value = false;
};

const cancelAction = () => {
  router.push('/home-tutor');
};

const getTutorIdFromToken = (token) => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.userId;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};
</script>

<style scoped>
input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
  filter: invert(50%);
}
</style>