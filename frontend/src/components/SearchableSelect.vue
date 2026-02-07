<template>
  <div class="relative" ref="dropdownRef">
    <!-- Input Field -->
    <div 
      class="relative cursor-pointer"
      @click="toggleDropdown"
    >
      <input
        ref="inputRef"
        v-model="searchQuery"
        @input="onSearchInput"
        @focus="isOpen = true"
        @keydown.down.prevent="navigateDown"
        @keydown.up.prevent="navigateUp"
        @keydown.enter.prevent="selectHighlighted"
        @keydown.escape="closeDropdown"
        :placeholder="placeholder"
        class="w-full px-4 py-2.5 pr-10 rounded-xl font-semibold shadow-lg border-2 transition-all duration-300 text-sm focus:outline-none focus:ring-2"
        :class="gradientClass"
      />
      
      <!-- Icon -->
      <div class="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
        <svg 
          class="w-5 h-5 text-gray-600 transition-transform duration-300"
          :class="{ 'rotate-180': isOpen }"
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
        </svg>
      </div>
    </div>

    <!-- Dropdown List -->
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0 translate-y-1"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-1"
    >
      <div
        v-if="isOpen && filteredOptions.length > 0"
        class="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-2xl border-2 max-h-60 overflow-y-auto"
        :class="borderClass"
      >
        <div
          v-for="(option, index) in filteredOptions"
          :key="option"
          @click="selectOption(option)"
          @mouseenter="highlightedIndex = index"
          class="px-4 py-2.5 cursor-pointer transition-all duration-150 text-sm font-medium"
          :class="{
            'bg-gradient-to-r text-white': highlightedIndex === index,
            'text-gray-700 hover:bg-gray-100': highlightedIndex !== index,
            [selectedGradientClass]: highlightedIndex === index
          }"
        >
          {{ option }}
        </div>
      </div>
    </transition>

    <!-- No Results -->
    <div
      v-if="isOpen && searchQuery && filteredOptions.length === 0"
      class="absolute z-50 w-full mt-2 bg-white rounded-xl shadow-2xl border-2 border-gray-300 p-4 text-center"
    >
      <p class="text-gray-500 text-sm">ไม่พบข้อมูล</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  options: {
    type: Array,
    required: true
  },
  placeholder: {
    type: String,
    default: 'เลือก...'
  },
  type: {
    type: String,
    default: 'default', // 'classType', 'grade', 'school', 'subject'
  }
});

const emit = defineEmits(['update:modelValue']);

const dropdownRef = ref(null);
const inputRef = ref(null);
const isOpen = ref(false);
const searchQuery = ref('');
const highlightedIndex = ref(0);

// Gradient styles based on type
const gradientClass = computed(() => {
  switch (props.type) {
    case 'classType':
      return 'bg-gradient-to-br from-pink-50 via-white to-purple-50 text-gray-800 border-pink-300 hover:border-pink-400 hover:shadow-xl focus:ring-pink-400 focus:border-pink-500';
    case 'grade':
      return 'bg-gradient-to-br from-amber-50 via-white to-orange-50 text-gray-800 border-amber-300 hover:border-amber-400 hover:shadow-xl focus:ring-amber-400 focus:border-amber-500';
    case 'school':
      return 'bg-gradient-to-br from-teal-50 via-white to-cyan-50 text-gray-800 border-teal-300 hover:border-teal-400 hover:shadow-xl focus:ring-teal-400 focus:border-teal-500';
    case 'subject':
      return 'bg-gradient-to-br from-purple-50 via-white to-indigo-50 text-gray-800 border-purple-300 hover:border-purple-400 hover:shadow-xl focus:ring-purple-400 focus:border-purple-500';
    default:
      return 'bg-gradient-to-br from-gray-50 via-white to-gray-50 text-gray-800 border-gray-300 hover:border-gray-400 hover:shadow-xl focus:ring-gray-400 focus:border-gray-500';
  }
});

const borderClass = computed(() => {
  switch (props.type) {
    case 'classType':
      return 'border-pink-300';
    case 'grade':
      return 'border-amber-300';
    case 'school':
      return 'border-teal-300';
    case 'subject':
      return 'border-purple-300';
    default:
      return 'border-gray-300';
  }
});

const selectedGradientClass = computed(() => {
  switch (props.type) {
    case 'classType':
      return 'from-pink-400 to-purple-500';
    case 'grade':
      return 'from-amber-400 to-orange-500';
    case 'school':
      return 'from-teal-400 to-cyan-500';
    case 'subject':
      return 'from-purple-400 to-indigo-500';
    default:
      return 'from-gray-400 to-gray-500';
  }
});

// Filter options based on search query
const filteredOptions = computed(() => {
  if (!searchQuery.value) {
    return props.options;
  }
  return props.options.filter(option =>
    option.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// Watch modelValue changes from parent
watch(() => props.modelValue, (newVal) => {
  if (!isOpen.value) {
    searchQuery.value = newVal;
  }
});

// Initialize search query with current value
onMounted(() => {
  searchQuery.value = props.modelValue;
});

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    inputRef.value?.focus();
    highlightedIndex.value = 0;
  }
};

const closeDropdown = () => {
  isOpen.value = false;
  // Restore selected value if search was cleared
  if (!searchQuery.value) {
    searchQuery.value = props.modelValue;
  }
};

const onSearchInput = () => {
  isOpen.value = true;
  highlightedIndex.value = 0;
  // Clear selection when typing
  if (searchQuery.value !== props.modelValue) {
    emit('update:modelValue', '');
  }
};

const selectOption = (option) => {
  searchQuery.value = option;
  emit('update:modelValue', option);
  isOpen.value = false;
};

const navigateDown = () => {
  if (highlightedIndex.value < filteredOptions.value.length - 1) {
    highlightedIndex.value++;
  }
};

const navigateUp = () => {
  if (highlightedIndex.value > 0) {
    highlightedIndex.value--;
  }
};

const selectHighlighted = () => {
  if (filteredOptions.value[highlightedIndex.value]) {
    selectOption(filteredOptions.value[highlightedIndex.value]);
  }
};

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
/* Custom scrollbar for dropdown */
div::-webkit-scrollbar {
  width: 8px;
}

div::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

div::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 10px;
}

div::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>