<template>
  <Teleport to="body">
    <Transition name="fade">
      <div
        v-if="show"
        class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-[60]"
        @click.self="handleCancel"
      >
        <Transition name="scale">
          <div
            v-if="show"
            class="bg-white rounded-2xl shadow-2xl max-w-md w-full overflow-hidden"
          >
            <!-- Header -->
            <div
              :class="[
                'p-6 flex items-center gap-4',
                variantClasses.header
              ]"
            >
              <div
                :class="[
                  'w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0',
                  variantClasses.icon
                ]"
              >
                <!-- Warning Icon -->
                <svg
                  v-if="variant === 'warning'"
                  class="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
                <!-- Danger Icon -->
                <svg
                  v-else-if="variant === 'danger'"
                  class="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <!-- Info Icon -->
                <svg
                  v-else
                  class="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <div class="flex-1">
                <h3 class="text-xl font-bold text-gray-900">
                  {{ title }}
                </h3>
              </div>
              <button
                v-if="showCloseButton"
                @click="handleCancel"
                class="p-1 hover:bg-gray-100 rounded-lg transition"
              >
                <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- Content -->
            <div class="p-6 pt-0">
              <p class="text-gray-700 leading-relaxed">
                {{ message }}
              </p>
              <p v-if="subMessage" class="text-sm text-gray-500 mt-2">
                {{ subMessage }}
              </p>

              <div v-if="showReasonInput" class="mt-4">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  หมายเหตุ
                </label>
                <select 
                  v-model="selectedReason"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">-- เลือกหมายเหตุ --</option>
                  <option 
                    v-for="option in reasonOptions" 
                    :key="option" 
                    :value="option"
                  >
                    {{ option }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Footer -->
            <div class="p-6 pt-0 flex gap-3">
              <button
                @click="handleCancel"
                :disabled="isLoading"
                class="flex-1 px-6 py-3 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {{ cancelText }}
              </button>
              <button
                @click="handleConfirm"
                :disabled="isLoading || !canConfirm"
                :class="[
                  'flex-1 px-6 py-3 font-semibold rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed',
                  variantClasses.button
                ]"
              >
                <span v-if="!isLoading">{{ confirmText }}</span>
                <span v-else class="flex items-center justify-center gap-2">
                  <svg class="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  กำลังดำเนินการ...
                </span>
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'ยืนยันการดำเนินการ'
  },
  message: {
    type: String,
    required: true
  },
  subMessage: {
    type: String,
    default: ''
  },
  confirmText: {
    type: String,
    default: 'ยืนยัน'
  },
  cancelText: {
    type: String,
    default: 'ยกเลิก'
  },
  variant: {
    type: String,
    default: 'warning',
    validator: (value) => ['warning', 'danger', 'info'].includes(value)
  },
  showCloseButton: {
    type: Boolean,
    default: true
  },
  showReasonInput: {
    type: Boolean,
    default: false
  },
  reasonOptions: {
    type: Array,
    default: () => ['น้องขอจบคอร์ส', 'น้องกลับมาเรียน', 'อื่น ๆ (แจ้งหัวหน้า)']
  }
});

const selectedReason = ref('');

const emit = defineEmits(['confirm', 'cancel', 'close']);

const isLoading = ref(false);

const variantClasses = computed(() => {
  switch (props.variant) {
    case 'danger':
      return {
        header: 'bg-red-50',
        icon: 'bg-red-100 text-red-600',
        button: 'bg-red-600 text-white hover:bg-red-700'
      };
    case 'warning':
      return {
        header: 'bg-yellow-50',
        icon: 'bg-yellow-100 text-yellow-600',
        button: 'bg-yellow-600 text-white hover:bg-yellow-700'
      };
    case 'info':
      return {
        header: 'bg-blue-50',
        icon: 'bg-blue-100 text-blue-600',
        button: 'bg-blue-600 text-white hover:bg-blue-700'
      };
    default:
      return {
        header: 'bg-gray-50',
        icon: 'bg-gray-100 text-gray-600',
        button: 'bg-gray-600 text-white hover:bg-gray-700'
      };
  }
});

const canConfirm = computed(() => {
  if (props.showReasonInput) {
    return selectedReason.value !== '';
  }
  return true;
});

const handleConfirm = () => {
  isLoading.value = true;
  emit('confirm', selectedReason.value);
};

const handleCancel = () => {
  if (!isLoading.value) {
    emit('cancel');
    emit('close');
  }
};

// Expose method to reset loading state
const resetLoading = () => {
  isLoading.value = false;
};

watch(() => props.show, (newValue) => {
  if (newValue) {
    selectedReason.value = '';
  }
});

defineExpose({ resetLoading });
</script>

<style scoped>
/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Scale transition */
.scale-enter-active,
.scale-leave-active {
  transition: all 0.2s ease;
}

.scale-enter-from,
.scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>