<template>
  <div v-if="show" class="fixed inset-0 z-[60] overflow-y-auto" @click="handleBackdropClick">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black/70 backdrop-blur-sm transition-opacity"></div>
    
    <!-- Modal Container -->
    <div class="flex min-h-full items-center justify-center p-4">
      <div 
        class="relative w-full max-w-md bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-2xl shadow-2xl transform transition-all border border-blue-500/30"
        @click.stop
      >
        <!-- Animated Background -->
        <div class="absolute inset-0 overflow-hidden rounded-2xl pointer-events-none">
          <div class="absolute w-32 h-32 bg-blue-500/20 rounded-full blur-3xl -top-16 -left-16 animate-pulse"></div>
          <div class="absolute w-32 h-32 bg-indigo-500/20 rounded-full blur-3xl -bottom-16 -right-16 animate-pulse delay-1000"></div>
        </div>

        <!-- Content -->
        <div class="relative z-10 p-6">
          <!-- Header -->
          <div class="flex items-center gap-3 mb-6">
            <div class="w-12 h-12 bg-blue-500/20 rounded-full flex items-center justify-center">
              <svg class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
            </div>
            <h3 class="text-xl font-bold text-white">กรอกรหัสใบเสร็จ</h3>
          </div>

          <!-- Input Field -->
          <div class="mb-4">
            <label class="block text-sm font-semibold text-gray-300 mb-2">
              รหัสใบเสร็จ (6 หลัก)
            </label>
            <input
              ref="inputRef"
              v-model="receiptCode"
              type="text"
              inputmode="numeric"
              maxlength="6"
              placeholder="000000"
              class="w-full px-4 py-3 bg-slate-800/50 text-white text-center text-2xl font-mono rounded-xl border-2 transition-all focus:outline-none focus:border-blue-500 tracking-widest"
              :class="error ? 'border-red-500' : 'border-slate-600'"
              @input="handleInput"
              @keyup.enter="handleConfirm"
            />
            <p v-if="error" class="text-red-400 text-sm mt-2 flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              {{ error }}
            </p>
            <p v-else class="text-gray-500 text-xs mt-2">
              กรุณากรอกตัวเลข 6 หลัก
            </p>
          </div>

          <!-- Visual Indicator -->
          <div class="flex justify-center gap-2 mb-6">
            <div
              v-for="i in 6"
              :key="i"
              class="w-10 h-12 rounded-lg flex items-center justify-center font-mono text-xl transition-all"
              :class="receiptCode.length >= i 
                ? 'bg-blue-500/30 text-white border-2 border-blue-500' 
                : 'bg-slate-800/30 text-gray-600 border-2 border-slate-700'"
            >
              {{ receiptCode[i - 1] || '-' }}
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-3">
            <button
              @click="handleCancel"
              :disabled="isSubmitting"
              class="flex-1 px-4 py-3 bg-slate-700 hover:bg-slate-600 text-white rounded-xl font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              ยกเลิก
            </button>
            <button
              @click="handleConfirm"
              :disabled="!isValid || isSubmitting"
              class="flex-1 px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <div v-if="isSubmitting" class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              <span>{{ isSubmitting ? 'กำลังบันทึก...' : 'ยืนยัน' }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';

const props = defineProps({
  show: Boolean,
  initialValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['confirm', 'cancel']);

const receiptCode = ref('');
const error = ref('');
const isSubmitting = ref(false);
const inputRef = ref(null);

const isValid = computed(() => {
  return receiptCode.value.length === 6 && /^\d{6}$/.test(receiptCode.value);
});

const handleInput = (event) => {
  // อนุญาตเฉพาะตัวเลข
  receiptCode.value = event.target.value.replace(/\D/g, '');
  error.value = '';
};

const handleConfirm = async () => {
  if (!isValid.value) {
    error.value = 'กรุณากรอกตัวเลข 6 หลัก';
    return;
  }

  isSubmitting.value = true;
  error.value = '';

  try {
    await emit('confirm', receiptCode.value);
    // ถ้าสำเร็จ parent จะปิด modal เอง
  } catch (err) {
    error.value = 'เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง';
    isSubmitting.value = false;
  }
};

const handleCancel = () => {
  if (!isSubmitting.value) {
    emit('cancel');
  }
};

const handleBackdropClick = (e) => {
  if (e.target === e.currentTarget && !isSubmitting.value) {
    emit('cancel');
  }
};

const resetForm = () => {
  receiptCode.value = '';
  error.value = '';
  isSubmitting.value = false;
};

watch(() => props.show, async (newVal) => {
  if (newVal) {
    receiptCode.value = props.initialValue || '';
    error.value = '';
    isSubmitting.value = false;
    await nextTick();
    inputRef.value?.focus();
  }
});
</script>

<style scoped>
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
</style>