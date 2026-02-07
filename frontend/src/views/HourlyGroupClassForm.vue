<template>
  <div class="mb-6 p-6 bg-gradient-to-br from-orange-50 to-amber-50 rounded-xl border-2 border-orange-300">
    <h3 class="text-xl font-bold text-orange-800 mb-4 flex items-center gap-2">
      <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
      เพิ่มกลุ่มรายชั่วโมงใหม่
    </h3>

    <form @submit.prevent="handleSubmit" class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Subject -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">วิชา *</label>
          <select
            v-model="formData.subjectId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500"
          >
            <option value="">เลือกวิชา</option>
            <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
              {{ subject.subjectName }}
            </option>
          </select>
        </div>

        <!-- Grade (NEW) -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">ระดับชั้น</label>
          <select
            v-model="formData.gradeId"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500"
          >
            <option value="">ไม่ระบุ</option>
            <option v-for="grade in grades" :key="grade.id" :value="grade.id">
              {{ grade.gradeName }}
            </option>
          </select>
        </div>

        <!-- Subtype -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">กลุ่ม *</label>
          <select
            v-model="formData.subtypeId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500"
          >
            <option value="">เลือกกลุ่ม</option>
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
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500"
          />
        </div>

        <!-- Hours Target -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">จำนวนชั่วโมงเป้าหมาย *</label>
          <input
            v-model="formData.hoursTarget"
            type="number"
            step="0.5"
            min="0.5"
            required
            placeholder="เช่น 20"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500"
          />
        </div>
      </div>

      <button
        type="submit"
        :disabled="isSubmitting"
        class="w-full px-6 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white font-bold rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all shadow-lg disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed flex items-center justify-center gap-2"
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
    required: true
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
  hoursTarget: ''
});

const handleSubmit = async () => {
  isSubmitting.value = true;
  try {
    const payload = {
      tutorId: props.tutorId,
      subjectId: formData.value.subjectId,
      gradeId: formData.value.gradeId || null,
      subtypeId: formData.value.subtypeId,
      className: formData.value.className || null,
      hoursTarget: parseFloat(formData.value.hoursTarget)
    };

    await api.post('/hourly-group-classes', payload);
    
    emit('success', 'เพิ่มกลุ่มรายชั่วโมงสำเร็จ!');
    
    // Reset form
    formData.value = {
      subjectId: '',
      gradeId: '',
      subtypeId: '',
      className: '',
      hoursTarget: ''
    };
  } catch (error) {
    console.error('Error creating hourly group class:', error);
    emit('error', error.response?.data?.message || 'ไม่สามารถเพิ่มคลาสได้');
  } finally {
    isSubmitting.value = false;
  }
};
</script>