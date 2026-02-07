<template>
  <div>
    <label class="block text-gray-700 font-semibold mb-3">
      ชั้น <span class="text-red-500">*</span>
    </label>

    <!-- ปุ่มเลือกชั้น -->
    <div class="flex flex-wrap gap-3">
      <button
        v-for="grade in grades"
        :key="grade"
        type="button"
        @click="selectGrade(grade)"
        class="px-5 py-2.5 rounded-full border font-medium transition-all duration-200 shadow-sm"
        :class="[
          modelValue === grade
            ? 'bg-gradient-to-r from-yellow-500 to-yellow-600 text-white border-yellow-600 shadow-md scale-105'
            : 'bg-white text-gray-700 border-gray-300 hover:bg-yellow-50 hover:border-yellow-400'
        ]"
      >
        {{ grade }}
      </button>
    </div>

    <p v-if="error" class="text-red-500 text-sm mt-2">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: String,
  error: String,
  customGradeValue: String
});

const emit = defineEmits(['update:modelValue', 'update:customGradeValue', 'clear-error']);

const grades = ['ม.1', 'ม.2', 'ม.3', 'ม.4', 'ม.5', 'ม.6', 'อื่น ๆ'];
const customGrade = ref(props.customGradeValue || '');

// อัปเดตค่าเมื่อพิมพ์ custom grade
watch(customGrade, (val) => emit('update:customGradeValue', val));

const selectGrade = (grade) => {
  emit('update:modelValue', grade);
  emit('clear-error', 'grade');
  if (grade !== 'อื่น ๆ') emit('update:customGradeValue', '');
};
</script>
