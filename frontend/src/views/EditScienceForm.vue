<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 to-green-100">
    <NavBar />

    <div class="container mx-auto px-4 lg:px-8 py-8 max-w-4xl">
      <div class="bg-gradient-to-r from-green-500 to-green-700 rounded-t-2xl shadow-xl text-center py-10">
        <h1 class="text-xl font-semibold text-white mb-2">แก้ไขแบบฟอร์มบันทึกการสอน</h1>
        <h1 class="text-5xl font-extrabold text-white drop-shadow-md">วิทยาศาสตร์ (รายเดือน)</h1>
      </div>

      <div v-if="loadingData" class="bg-white rounded-b-2xl shadow-2xl p-8">
        <div class="text-center text-gray-500 py-8">
          <p class="text-lg">กำลังโหลดข้อมูล...</p>
        </div>
      </div>

      <div v-else class="bg-white rounded-b-2xl shadow-2xl p-8">
        <div class="space-y-6">
          <!-- คลาสที่สอน - เปลี่ยนเป็น dropdown -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              คลาสที่สอน <span class="text-red-500">*</span>
            </label>
            <div class="w-full px-4 py-3 bg-gray-100 border border-gray-300 rounded-lg text-gray-700">
              {{ getClassName(formData.classId) || '-' }}
            </div>
            <p class="text-sm text-gray-500 mt-1">ไม่สามารถแก้ไขคลาสได้ หากต้องการเปลี่ยน กรุณาสร้างฟอร์มใหม่</p>
            <p v-if="errors.classId" class="text-red-500 text-sm mt-1">{{ errors.classId }}</p>
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
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
            <div class="w-full px-4 py-3 bg-gray-100 border border-gray-300 rounded-lg text-gray-700">
              {{ formatThaiDate(formData.teachingDate) || '-' }}
            </div>
            <p class="text-sm text-gray-500 mt-1">ไม่สามารถแก้ไขวันที่สอนได้ หากต้องการเปลี่ยน กรุณาแจ้งแอดมิน</p>
            <p v-if="errors.teachingDate" class="text-red-500 text-sm mt-1">{{ errors.teachingDate }}</p>
          </div>

          <!-- จำนวนชั่วโมงที่สอน - เปลี่ยนเป็น input -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">
              จำนวนชั่วโมงที่สอน <span class="text-red-500">*</span>
            </label>
            <div class="w-full px-4 py-3 bg-gray-100 border border-gray-300 rounded-lg text-gray-700">
              {{ formData.hoursTaught || '-' }}
            </div>
            <p class="text-sm text-gray-500 mt-1">ไม่สามารถแก้ไขจำนวนชั่วโมงได้ หากต้องการเปลี่ยน กรุณาแจ้งแอดมิน</p>
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
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
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
              placeholder="หมายเหตุเพิ่มเติม (ถ้ามี)"
            ></textarea>
          </div>

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
              type="button"
              @click="handleUpdate"
              :disabled="!isFormValid || isSubmitting || !isChanged"
              class="px-8 py-3 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? 'กำลังบันทึก...' : 'บันทึกการแก้ไข' }}
            </button>
          </div>
        </div>

        <UpdateFormSuccessModal
          :visible="showModal"
          @confirm="handleModalConfirm"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import NavBar from '@/components/NavBar.vue';
import UpdateFormSuccessModal from '@/modals/UpdateFormSuccessModal.vue';
import api from '@/api.js';

const router = useRouter();
const route = useRoute();
const id = route.params.id;

const loadingData = ref(true);
const isSubmitting = ref(false);
const showModal = ref(false);
const originalData = ref(null);
const isChanged = ref(false);
const monthlyClasses = ref([]);

const getClassName = (classId) => {
  const cls = monthlyClasses.value.find(c => c.id === classId);
  return cls ? cls.className : '';
};

const formatThaiDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('th-TH', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
};

const formData = ref({
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
  classId: '',
  content: '',
  teachingDate: '',
  hoursTaught: '',
  studentsPresent: '',
  studentsAbsent: ''
});

const isFormValid = computed(() => {
  return (
    formData.value.classId !== null &&
    formData.value.content.trim() !== '' &&
    formData.value.teachingDate !== '' &&
    formData.value.hoursTaught !== null &&
    formData.value.studentsPresent !== null &&
    formData.value.studentsAbsent !== null &&
    Object.values(errors.value).every(e => e === '')
  );
});

const loadMonthlyClasses = async () => {
  try {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    const tutorId = getTutorIdFromToken(token);
    const response = await api.get(`/monthly-classes?tutorId=${tutorId}&active=true`);
    
    monthlyClasses.value = response.data
      .filter(cls => !cls.className.startsWith('คณิตศาสตร์'))
      .map(cls => ({
        id: cls.id,
        className: cls.className,
        subjectId: cls.subject?.id || cls.subjectId,
        subjectName: cls.subject?.subjectName || cls.subjectName || '-',
        grade: cls.grade?.gradeName || cls.grade || '-'
      }));

  } catch (error) {
    console.error('Error loading monthly classes:', error);
    alert('เกิดข้อผิดพลาดในการโหลดข้อมูลคลาส');
  }
};

onMounted(async () => {
  try {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (!token) {
      router.push('/login');
      return;
    }

    // โหลดคลาสก่อน
    await loadMonthlyClasses();

    // แล้วโหลดข้อมูลฟอร์ม
    const response = await api.get(`/science-forms/${id}`);
    const data = response.data;

    formData.value = {
      classId: data.classId,
      subjectId: data.subjectId,
      content: data.content || '',
      teachingDate: data.teachingDate || '',
      hoursTaught: data.hoursTaught !== null ? Number(data.hoursTaught) : null,
      studentsPresent: data.studentsPresent !== null ? Number(data.studentsPresent) : null,
      studentsAbsent: data.studentsAbsent !== null ? Number(data.studentsAbsent) : null,
      remark: data.remark || ''
    };

    // เก็บ original data
    originalData.value = JSON.parse(JSON.stringify(formData.value));

  } catch (error) {
    console.error('Error loading form:', error);
    if (error.response?.status === 403 || error.response?.status === 401) {
      alert('Session หมดอายุ กรุณา Login ใหม่');
      router.push('/login');
    } else {
      alert('โหลดข้อมูลล้มเหลว: ' + (error.response?.data?.message || error.message));
      router.push('/list-hour-forms');
    }
  } finally {
    loadingData.value = false;
  }
});

const clearError = (fieldName) => {
  const value = formData.value[fieldName];
  if (value && !(typeof value === 'string' && value.trim() === '')) {
    errors.value[fieldName] = '';
  }
};

const checkChanged = () => {
  if (!originalData.value) return false;

  const editableFields = ['content', 'studentsPresent', 'studentsAbsent', 'remark'];
  
  for (const field of editableFields) {
    const originalValue = originalData.value[field];
    const currentValue = formData.value[field];
    
    if (typeof originalValue === 'number' || typeof currentValue === 'number') {
      if (Number(originalValue) !== Number(currentValue)) {
        return true;
      }
    } else {
      if (String(originalValue || '').trim() !== String(currentValue || '').trim()) {
        return true;
      }
    }
  }
  
  return false;
};

watch(formData, () => {
  isChanged.value = checkChanged();
}, { deep: true });

const handleUpdate = async () => {
  // Validate required fields
  const requiredFields = ['classId', 'content', 'teachingDate', 'hoursTaught', 'studentsPresent', 'studentsAbsent'];
  let hasError = false;

  requiredFields.forEach(field => {
    const value = formData.value[field];
    if (value === null || value === undefined || (typeof value === 'string' && value.trim() === '')) {
      errors.value[field] = 'จำเป็นต้องกรอกข้อมูลนี้';
      hasError = true;
    }
  });

  if (hasError) {
    alert('กรุณากรอกข้อมูลให้ครบถ้วน');
    return;
  }

  try {
    isSubmitting.value = true;

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

    await api.put(`/science-forms/${id}`, dataToSubmit);
    
    showModal.value = true;

  } catch (error) {
    console.error('Error updating form:', error);
    if (error.response?.status === 403) {
      alert('คุณไม่มีสิทธิ์แก้ไขข้อมูลนี้');
    } else if (error.response?.status === 401) {
      alert('Session หมดอายุ กรุณา Login ใหม่');
      router.push('/login');
    } else {
      alert('แก้ไขข้อมูลล้มเหลว: ' + (error.response?.data?.message || error.message));
    }
  } finally {
    isSubmitting.value = false;
  }
};

const handleCancel = () => {
  router.push('/list-hour-forms');
};

const handleModalConfirm = () => {
  router.push('/list-hour-forms');
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