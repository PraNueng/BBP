<template>
  <div
    v-if="visible"
    class="fixed inset-0 flex items-center justify-center backdrop-blur-sm bg-black/30 z-50"
  >
    <div
      class="bg-white rounded-2xl shadow-2xl w-11/12 sm:w-96 p-8 text-center relative"
    >
      <div class="flex justify-center mb-4">
        <div
          v-if="type === 'success'"
          class="w-20 h-20 flex items-center justify-center bg-green-100 rounded-full"
        >
          <svg
            class="w-10 h-10 text-green-500"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        </div>

        <div
          v-else-if="type === 'error'"
          class="w-20 h-20 flex items-center justify-center bg-red-100 rounded-full"
        >
          <svg
            class="w-10 h-10 text-red-500"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
      </div>

      <h2
        class="text-2xl font-bold mb-2"
        :class="type === 'success' ? 'text-green-600' : 'text-red-600'"
      >
        {{ type === 'success' ? 'บันทึกสำเร็จ' : 'ไม่สำเร็จ' }}
      </h2>

      <p class="text-gray-600 mb-6">{{ message }}</p>

      <div class="flex justify-center space-x-4">
        <button
          @click="goHome"
          class="px-6 py-2 bg-white border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-100 transition"
        >
          กลับสู่หน้าหลัก
        </button>
        <button
          @click="nextAction"
          class="px-6 py-2 bg-gradient-to-r from-blue-500 to-blue-700 text-white font-semibold rounded-lg hover:from-blue-600 hover:to-blue-800 transition"
        >
          ประเมินต่อ
        </button>
      </div>
      
      <button
        class="absolute top-1 right-5 text-gray-400 hover:text-gray-600"
        @click="cancelAction"
      >
        ✕
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  visible: Boolean,
  type: {
    type: String,
    default: 'success'
  },
  message: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['continue', 'close', 'cancel']);
const router = useRouter();

const closeModal = () => emit('close');
const goHome = () => router.push('/home-tutor');
const nextAction = () => {
    emit('continue');
    emit('close');
    setTimeout(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, 50);
};
const cancelAction = () => {
  emit('cancel');
  emit('close');
};
</script>
