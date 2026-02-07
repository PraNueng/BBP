<template>
  <div class="bg-gradient-to-r from-purple-50 to-indigo-50 rounded-xl p-4 mb-6 border-2 border-purple-200">
    <div class="flex items-center justify-between mb-3">
      <h3 class="text-lg font-bold text-gray-800 flex items-center gap-2">
        <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
        </svg>
        กรองข้อมูล
      </h3>
      
      <!-- Clear All Filters Button -->
      <button
        v-if="hasActiveFilters"
        @click="clearAllFilters"
        class="px-3 py-1 text-sm bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition font-semibold flex items-center gap-1"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
        ล้างทั้งหมด
      </button>
    </div>

    <!-- Search Bar -->
    <div class="mb-4">
      <SearchBar
        v-model="filters.keyword"
        placeholder="ค้นหาชื่อคลาส ชื่อวิชา หรือชื่อครู"
        @search="emitFilters"
      />
    </div>

    <!-- Filter Dropdowns -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
      <!-- Subject Filter -->
      <div>
        <label class="block text-sm font-semibold text-gray-700 mb-1">วิชา</label>
        <select
          v-model="filters.subjectId"
          @change="emitFilters"
          class="w-full px-3 py-2 border-2 border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition"
        >
          <option :value="null">ทุกวิชา</option>
          <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
            {{ subject.subjectName }}
          </option>
        </select>
      </div>

      <!-- Grade Filter -->
      <div>
        <label class="block text-sm font-semibold text-gray-700 mb-1">ระดับชั้น</label>
        <select
          v-model="filters.gradeId"
          @change="emitFilters"
          class="w-full px-3 py-2 border-2 border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition"
        >
          <option :value="null">ทุกระดับ</option>
          <option v-for="grade in grades" :key="grade.id" :value="grade.id">
            {{ grade.gradeName }}
          </option>
        </select>
      </div>
    </div>

    <!-- Active Filters Summary -->
    <div v-if="hasActiveFilters" class="mt-3 flex flex-wrap gap-2">
      <span class="text-xs font-semibold text-gray-600">กำลังกรอง:</span>
      
      <span v-if="filters.keyword" class="inline-flex items-center gap-1 px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
        "{{ filters.keyword }}"
        <button @click="clearFilter('keyword')" class="hover:bg-blue-200 rounded-full p-0.5">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </span>

      <span v-if="filters.subjectId" class="inline-flex items-center gap-1 px-2 py-1 bg-purple-100 text-purple-700 rounded-full text-xs font-medium">
        {{ getSubjectName(filters.subjectId) }}
        <button @click="clearFilter('subjectId')" class="hover:bg-purple-200 rounded-full p-0.5">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </span>

      <span v-if="filters.gradeId" class="inline-flex items-center gap-1 px-2 py-1 bg-indigo-100 text-indigo-700 rounded-full text-xs font-medium">
        {{ getGradeName(filters.gradeId) }}
        <button @click="clearFilter('gradeId')" class="hover:bg-indigo-200 rounded-full p-0.5">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import SearchBar from '@/components/SearchBar.vue';

const props = defineProps({
  subjects: {
    type: Array,
    default: () => []
  },
  grades: {
    type: Array,
    default: () => []
  },
  initialFilters: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['filter']);

const filters = ref({
  subjectId: props.initialFilters.subjectId || null,
  gradeId: props.initialFilters.gradeId || null,
  keyword: props.initialFilters.keyword || ''
});

const hasActiveFilters = computed(() => {
  return filters.value.subjectId !== null ||
         filters.value.gradeId !== null ||
         filters.value.keyword !== '';
});

const getSubjectName = (id) => {
  return props.subjects.find(s => s.id === id)?.subjectName || '';
};

const getGradeName = (id) => {
  return props.grades.find(g => g.id === id)?.gradeName || '';
};

const emitFilters = () => {
  emit('filter', { ...filters.value });
};

const clearFilter = (key) => {
  if (key === 'keyword') {
    filters.value[key] = '';
  } else {
    filters.value[key] = null;
  }
  emitFilters();
};

const clearAllFilters = () => {
  filters.value = {
    subjectId: null,
    gradeId: null,
    keyword: ''
  };
  emitFilters();
};

// Watch for external filter changes
watch(() => props.initialFilters, (newFilters) => {
  filters.value = {
    subjectId: newFilters.subjectId || null,
    gradeId: newFilters.gradeId || null,
    keyword: newFilters.keyword || ''
  };
}, { deep: true });
</script>