<template>
  <div class="relative" ref="wrapperRef">
    <!-- Input -->
    <div
      class="relative cursor-pointer"
      @click="toggle"
    >
      <input
        readonly
        :value="modelValue"
        :placeholder="placeholder"
        class="w-full px-4 py-2.5 pr-10 rounded-xl font-semibold shadow border-2 text-sm cursor-pointer bg-white"
      />

      <!-- Icon -->
      <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
        <svg
          class="w-5 h-5 text-gray-600 transition-transform duration-200"
          :class="{ 'rotate-180': isOpen }"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
        </svg>
      </div>
    </div>

    <!-- Dropdown -->
    <div
      v-if="isOpen"
      class="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-lg border max-h-60 overflow-y-auto"
    >
      <div
        v-for="option in options"
        :key="option"
        @click="select(option)"
        class="px-4 py-2.5 cursor-pointer text-sm hover:bg-gray-100"
      >
        {{ option }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

defineProps({
  modelValue: String,
  options: {
    type: Array,
    required: true
  },
  placeholder: {
    type: String,
    default: 'เลือก...'
  }
});

const emit = defineEmits(['update:modelValue']);

const isOpen = ref(false);
const wrapperRef = ref(null);

const toggle = () => {
  isOpen.value = !isOpen.value;
};

const select = (option) => {
  emit('update:modelValue', option);
  isOpen.value = false;
};

const handleClickOutside = (e) => {
  if (wrapperRef.value && !wrapperRef.value.contains(e.target)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
