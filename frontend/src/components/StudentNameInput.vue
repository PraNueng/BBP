<template>
  <div class="relative">
    <label class="block text-gray-700 font-semibold mb-2">
      ชื่อน้อง <span class="text-red-500">*</span>
    </label>
    <input
      :value="modelValue"
      @input="handleInput"
      @focus="showSuggestions = true"
      @blur="hideSuggestions"
      type="text"
      required
      class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500"
      :class="{ 'border-red-500': error }"
      placeholder="พิมพ์ชื่อน้อง เลือกรายชื่อที่ระบบมีให้"
      autocomplete="off"
    />
    
    <!-- Suggestions Dropdown -->
    <div
      v-if="showSuggestions && filteredStudents.length > 0"
      class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto"
    >
      <div
        v-for="student in filteredStudents"
        :key="student.id"
        @mousedown.prevent="selectStudent(student)"
        class="px-4 py-3 hover:bg-yellow-50 cursor-pointer border-b border-gray-100 last:border-b-0"
      >
        <div class="font-medium text-gray-900">{{ student.displayName }}</div>
        <div class="text-sm text-gray-500">{{ student.grade }}</div>
      </div>
    </div>
    
    <p v-if="error" class="text-red-500 text-sm mt-1">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  modelValue: String,
  students: Array,
  error: String
});

const emit = defineEmits(['update:modelValue', 'select-student', 'clear-error', 'blur']);

const showSuggestions = ref(false);

const filteredStudents = computed(() => {
  const query = props.modelValue.toLowerCase().trim();
  
  if (query.length === 0) {
    return props.students.slice(0, 10);
  }
  
  // ค้นหาทั้งชื่อเล่น, ชื่อจริง, และ displayName
  return props.students
    .filter(s => {
      const displayName = (s.displayName || '').toLowerCase();
      const studentName = (s.studentName || '').toLowerCase();
      const nickname = (s.nickname || '').toLowerCase();
      const firstName = (s.firstName || '').toLowerCase();
      
      return displayName.includes(query) || 
             studentName.includes(query) || 
             nickname.includes(query) ||
             firstName.includes(query);
    })
    .slice(0, 10);
});

const handleInput = (e) => {
  emit('update:modelValue', e.target.value);
  showSuggestions.value = true;
};

const selectStudent = (student) => {
  emit('update:modelValue', student.displayName);
  emit('select-student', student);
  showSuggestions.value = false;
};

const hideSuggestions = () => {
  setTimeout(() => {
    showSuggestions.value = false;
  }, 200);
};
</script>