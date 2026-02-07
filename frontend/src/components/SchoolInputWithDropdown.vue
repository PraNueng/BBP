<template>
  <div class="relative" ref="wrapperRef">
    <input
      v-model="inputValue"
      @focus="isOpen = true"
      @input="isOpen = true"
      :placeholder="placeholder"
      class="w-full px-4 py-2.5 rounded-xl font-semibold shadow border-2 border-black text-sm bg-white"
    />

    <!-- Clear button -->
    <button
        v-if="inputValue"
        @mousedown.prevent
        @click="clearInput"
        class="scale-60 absolute right-0 top-1/2 -translate-y-1/2 bg-red-100 w-12 h-6 rounded-full text-red-700 font-bold hover:text-gray-700"
    >
        ✕
    </button>

    <!-- Dropdown -->
    <div
      v-if="isOpen && filteredOptions.length"
      class="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-lg border max-h-60 overflow-y-auto"
    >
      <div
        v-for="option in filteredOptions"
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
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  modelValue: String,
  options: {
    type: Array,
    required: true
  },
  placeholder: {
    type: String,
    default: 'โรงเรียน'
  }
});

const emit = defineEmits(['update:modelValue']);

const inputValue = ref(props.modelValue || '');
const isOpen = ref(false);
const wrapperRef = ref(null);

const clearInput = () => {
  inputValue.value = '';
  emit('update:modelValue', '');
  isOpen.value = true;
};

watch(() => props.modelValue, (val) => {
  inputValue.value = val;
});

watch(inputValue, (val) => {
  emit('update:modelValue', val);
});

const filteredOptions = computed(() => {
  if (!inputValue.value) return props.options;
  return props.options.filter(o =>
    o.toLowerCase().includes(inputValue.value.toLowerCase())
  );
});

const select = (option) => {
  inputValue.value = option;
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
