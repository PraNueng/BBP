<template>
  <div class="bg-gradient-to-br from-white to-gray-50 border-2 border-gray-200 rounded-xl p-5 hover:border-purple-300 hover:shadow-xl transition-all">
    <div class="flex items-start justify-between gap-3 mb-3">
      <div class="flex-1">
        <h4 class="text-lg font-bold text-gray-800 mb-1">
          {{ getClassTitle() }}
        </h4>
        <span
          :class="[
            'inline-block px-2 py-1 rounded-full text-xs font-bold',
            classData.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
          ]"
        >
          {{ classData.isActive ? '✓ ใช้งาน' : '✗ ปิด' }}
        </span>
      </div>
      
      <!-- Manage Tutors Button -->
      <button
        @click="$emit('manage-tutors')"
        class="p-2 rounded-lg bg-gradient-to-r from-blue-500 to-indigo-500 text-white hover:from-blue-600 hover:to-indigo-600 transition shadow-md hover:shadow-lg flex items-center justify-center"
        title="จัดการครู"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
      </button>
    </div>
    
    <div class="space-y-2 text-sm">
      <!-- Subject -->
      <div class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">วิชา:</span>
          <span class="text-gray-800 ml-1">{{ classData.subjectName }}</span>
        </div>
      </div>

      <!-- Grade -->
      <div class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">ชั้น:</span>
          <span class="px-2 py-0.5 ml-1 bg-purple-100 text-purple-700 rounded text-xs font-medium">
            {{ classData.gradeName || 'ไม่ระบุ' }}
          </span>
        </div>
      </div>

      <!-- Type -->
      <div v-if="classData.subtypeName" class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">ประเภท:</span>
          <span class="text-gray-800 ml-1">{{ classData.subtypeName }}</span>
        </div>
      </div>

      <!-- Tutors (for monthly) or Single Tutor (for hourly) -->
      <div class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <div class="flex-1">
          <span class="text-gray-600 font-medium">ครู:</span>
          
          <!-- Monthly & Houtly-Group: Multiple tutors -->
          <div v-if="(classType === 'monthly' || classType === 'hourly-group' || classType === 'hourly-individual') && classData.tutors && classData.tutors.length > 0" class="mt-1 flex flex-wrap gap-1">
            <span
              v-for="tutor in classData.tutors.slice(0, 3)"
              :key="tutor.id"
              class="px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs font-medium"
            >
              {{ tutor.nickname }}
            </span>
            <span
              v-if="classData.tutors.length > 3"
              class="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs font-medium"
            >
              +{{ classData.tutors.length - 3 }}
            </span>
          </div>
          
          <!-- Hourly: Single tutor -->
          <span v-else-if="classType !== 'monthly' && classData.tutorName" class="px-2 py-0.5 ml-1 bg-blue-100 text-blue-700 rounded text-xs font-medium">
            {{ classData.tutorName }}
          </span>
          
          <!-- No tutor -->
          <span v-else class="text-gray-400 italic ml-1">ยังไม่มีครู</span>
        </div>
      </div>

      <!-- Students Count (for group classes) -->
      <div v-if="classType === 'monthly' || classType === 'hourly-group'" class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">นักเรียน:</span>
          <span class="px-2 py-0.5 ml-1 bg-green-100 text-green-700 rounded text-xs font-medium">
            {{ classData.totalStudents || 0 }} คน
          </span>
        </div>
      </div>

      <!-- Student Name (for individual class) -->
      <div v-if="classType === 'hourly-individual'" class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">นักเรียน:</span>
          <span class="text-gray-800 ml-1">
            {{ classData.studentNickname || classData.studentFirstName }}
            {{ classData.studentLastName || '' }}
          </span>
        </div>
      </div>

      <!-- Hours Target (for hourly classes) -->
      <div v-if="classType !== 'monthly' && classData.hoursTarget" class="flex items-start gap-2">
        <svg class="w-4 h-4 text-gray-500 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div>
          <span class="text-gray-600 font-medium">เป้าหมาย:</span>
          <span class="text-gray-800 ml-1">{{ classData.hoursTarget }} ชั่วโมง</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  classData: {
    type: Object,
    required: true
  },
  classType: {
    type: String,
    required: true,
    validator: (value) => ['monthly', 'hourly-group', 'hourly-individual'].includes(value)
  }
});

defineEmits(['manage-tutors']);

const getClassTitle = () => {
  if (props.classData.className) {
    return props.classData.className;
  }
  
  if (props.classType === 'monthly' || props.classType === 'hourly-group') {
    return `${props.classData.subjectName} - ${props.classData.subtypeName || ''}`;
  }
  
  if (props.classType === 'hourly-individual') {
    const studentName = props.classData.studentNickname || props.classData.studentFirstName || 'นักเรียน';
    return `${props.classData.subjectName} - ${studentName}`;
  }
  
  return 'คลาส';
};
</script>