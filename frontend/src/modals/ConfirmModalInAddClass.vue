<template>
  <ModalShell 
    v-if="show" 
    @close="handleCancel" 
    :showBackground="true" 
    wrapperClass="bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 w-full max-w-md relative rounded-3xl shadow-2xl"
  >
    <template #icon>
      <div class="p-2 bg-white/20 rounded-lg">
        <component :is="iconComponent" class="w-6 h-6 text-white" />
      </div>
    </template>
    <template #title>{{ title }}</template>
    <template #closeIcon>
      <X class="w-6 h-6 group-hover:rotate-90 transition-transform" />
    </template>
    
    <div class="relative z-10 p-6">
      <div class="text-white text-base leading-relaxed whitespace-pre-line">
        {{ message }}
      </div>
      
      <div class="flex gap-4 justify-end pt-6 mt-6 border-t border-white/10">
        <button
          @click="handleCancel"
          class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
        >
          {{ cancelText }}
        </button>
        <button
          @click="handleConfirm"
          :class="[
            'px-6 py-3 font-semibold rounded-xl text-white transition-all shadow-lg transform hover:scale-105 flex items-center gap-2',
            type === 'danger' 
              ? 'bg-gradient-to-r from-red-600 to-pink-700 hover:from-red-700 hover:to-pink-800'
              : 'bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-emerald-800'
          ]"
        >
          <component :is="confirmIconComponent" class="w-5 h-5" />
          {{ confirmText }}
        </button>
      </div>
    </div>
    
    <template #footer>
      <!-- footer intentionally empty -->
    </template>
  </ModalShell>
</template>

<script setup>
import { computed } from 'vue';
import { X, AlertCircle, CheckCircle, AlertTriangle } from 'lucide-vue-next';
import ModalShell from '../components/ModalShell.vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'ยืนยันการดำเนินการ'
  },
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'info', // 'info', 'warning', 'danger', 'success'
    validator: (value) => ['info', 'warning', 'danger', 'success'].includes(value)
  },
  confirmText: {
    type: String,
    default: 'ตกลง'
  },
  cancelText: {
    type: String,
    default: 'ยกเลิก'
  }
});

const emit = defineEmits(['confirm', 'cancel', 'close']);

const iconComponent = computed(() => {
  switch (props.type) {
    case 'danger':
      return AlertCircle;
    case 'warning':
      return AlertTriangle;
    case 'success':
      return CheckCircle;
    default:
      return AlertCircle;
  }
});

const confirmIconComponent = computed(() => {
  return props.type === 'danger' ? AlertCircle : CheckCircle;
});

const handleConfirm = () => {
  emit('confirm');
  emit('close');
};

const handleCancel = () => {
  emit('cancel');
  emit('close');
};
</script>