<template>
  <div class="mb-6 p-6 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border-2 border-green-300">
    <h3 class="text-xl font-bold text-green-800 mb-4 flex items-center gap-2">
      <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
      เพิ่มคลาสรายเดือนใหม่
    </h3>

    <form @submit.prevent="handleSubmit" class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Subject -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">วิชา *</label>
          <select
            v-model="formData.subjectId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          >
            <option value="">เลือกวิชา</option>
            <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
              {{ subject.name }}
            </option>
          </select>
        </div>

        <!-- Grade -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">ระดับชั้น</label>
          <select
            v-model="formData.gradeId"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          >
            <option value="">ไม่ระบุ</option>
            <option v-for="grade in grades" :key="grade.id" :value="grade.id">
              {{ grade.gradeName }}
            </option>
          </select>
        </div>

        <!-- Subtype -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">ประเภท *</label>
          <select
            v-model="formData.subtypeId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          >
            <option value="">เลือกประเภท</option>
            <option v-for="subtype in subtypes" :key="subtype.id" :value="subtype.id">
              {{ subtype.subtypeName }}
            </option>
          </select>
        </div>

        <!-- Class Name -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">ชื่อคลาส</label>
          <input
            v-model="formData.className"
            type="text"
            placeholder="ระบุชื่อคลาส (ถ้ามี)"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
        </div>

        <!-- Start Date -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">วันที่เริ่ม</label>
          <input
            v-model="formData.startDate"
            type="date"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
        </div>

        <!-- End Date -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">วันที่สิ้นสุด</label>
          <input
            v-model="formData.endDate"
            type="date"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          />
        </div>
      </div>

      <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div class="flex items-start gap-2">
          <svg class="w-5 h-5 text-blue-600 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="text-sm text-blue-800">
            <strong>หมายเหตุ:</strong> คลาสจะถูกสร้างโดยไม่มีครูผู้สอน สามารถเพิ่มครูได้ในภายหลังผ่านปุ่ม "จัดการครู"
          </p>
        </div>
      </div>

      <button
        type="submit"
        :disabled="isSubmitting"
        class="w-full px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-bold rounded-lg hover:from-green-600 hover:to-green-700 transition-all shadow-lg disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed flex items-center justify-center gap-2"
      >
        <svg v-if="!isSubmitting" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        <svg v-else class="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        {{ isSubmitting ? 'กำลังเพิ่ม...' : 'เพิ่มคลาส' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import api from '@/api.js';

const props = defineProps({
  tutorId: {
    type: Number,
    required: false  // ไม่จำเป็นอีกต่อไป
  },
  subjects: {
    type: Array,
    required: true
  },
  subtypes: {
    type: Array,
    required: true
  },
  grades: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['success', 'error']);

const isSubmitting = ref(false);
const formData = ref({
  subjectId: '',
  gradeId: '',
  subtypeId: '',
  className: '',
  startDate: '',
  endDate: ''
});

const handleSubmit = async () => {
  isSubmitting.value = true;
  try {
    // ✅ ลบ tutorId ออกจาก payload
    const payload = {
      subjectId: formData.value.subjectId,
      gradeId: formData.value.gradeId || null,
      subtypeId: formData.value.subtypeId,
      className: formData.value.className || null,
      startDate: formData.value.startDate || null,
      endDate: formData.value.endDate || null
    };

    await api.post('/monthly-classes', payload);
    
    emit('success', 'เพิ่มคลาสรายเดือนสำเร็จ! สามารถเพิ่มครูได้จากรายการคลาส');
    
    // Reset form
    formData.value = {
      subjectId: '',
      gradeId: '',
      subtypeId: '',
      className: '',
      startDate: '',
      endDate: ''
    };
  } catch (error) {
    console.error('Error creating monthly class:', error);
    emit('error', error.response?.data?.message || 'ไม่สามารถเพิ่มคลาสได้');
  } finally {
    isSubmitting.value = false;
  }
};
</script>