
<template>
  <div
    v-if="visible"
    class="fixed inset-0 flex items-center justify-center backdrop-blur-sm bg-black/40 z-50 p-4"
    @click.self="$emit('close')"
  >
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[85vh] overflow-hidden flex flex-col">
      <!-- Header -->
      <div class="sticky top-0 bg-gradient-to-r from-yellow-500 to-yellow-700 p-6 rounded-t-2xl z-10">
        <div class="flex-col">
          <div class="flex items-center justify-center">
            <div class="flex items-center gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h2 class="text-2xl sm:text-3xl font-bold text-white">ประวัติการแก้ไข</h2>
            </div>
          </div>
          <div>
            <button 
              @click="$emit('close')" 
              class="absolute right-2 top-2 text-white hover:bg-white/20 rounded-full w-12 p-2 transition-all"
            >
              <svg class="w-8 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <ul v-if="history.length > 0" class="space-y-4">
          <li v-for="(h, i) in history" :key="i" 
            class="bg-gradient-to-r from-gray-50 to-yellow-50 p-5 rounded-xl border-l-4 border-yellow-500 shadow-sm hover:shadow-md transition-all"
          >
            <div class="flex items-start gap-4">
              <div class="flex-shrink-0 w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </div>
              
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span class="px-3 py-1 bg-yellow-600 text-white text-sm font-semibold rounded-full">
                    แก้ไข{{ getFieldLabel(h.fieldName) }}
                  </span>
                </div>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-3">
                  <div class="bg-white p-3 rounded-lg border border-gray-200">
                    <p class="text-xs text-gray-500 font-semibold mb-1 text-center">ข้อมูลเดิม:</p>
                    <p class="text-gray-700 font-medium break-words text-center">
                      {{ formatValue(h.fieldName, h.oldValue) || '-' }}
                    </p>
                  </div>
                  <div class="bg-white p-3 rounded-lg border border-green-200">
                    <p class="text-xs text-green-600 font-semibold mb-1 text-center">ข้อมูลใหม่:</p>
                    <p class="text-green-700 font-medium break-words text-center">
                      {{ formatValue(h.fieldName, h.newValue) || '-' }}
                    </p>
                  </div>
                </div>
                
                <div class="flex items-center gap-4 text-sm text-gray-600">
                  <div class="flex items-center gap-2 pr-1" v-if="h.updatedBy">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    <span class="font-medium">{{ h.updatedBy }}</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span>{{ formatDate(h.updatedAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
        
        <div v-else class="text-center py-16">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-20 w-20 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p class="text-gray-500 text-lg font-medium">ยังไม่มีประวัติการแก้ไข</p>
          <p class="text-gray-400 text-sm mt-2">ประวัติจะแสดงเมื่อมีการแก้ไขข้อมูลฟอร์ม</p>
        </div>
      </div>

      <!-- Footer -->
      <div class="sticky bottom-0 bg-gray-50 px-6 py-4 border-t border-gray-200 rounded-b-2xl">
        <div class="flex justify-end">
          <button
            @click="$emit('close')"
            class="px-6 py-3 bg-gradient-to-r from-gray-600 to-gray-700 text-white font-semibold rounded-xl hover:from-gray-700 hover:to-gray-800 transition-all shadow-md hover:shadow-lg transform hover:scale-105"
          >
            ปิด
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  history: {
    type: Array,
    default: () => []
  }
});

defineEmits(['close']);

const getFieldLabel = (fieldName) => {
  const labels = {
    studentName: 'ชื่อน้อง',
    grade: 'ชั้น',
    subject: 'วิชาที่สอน',
    content: 'เนื้อหาที่สอน',
    teachingDate: 'วันที่สอน',
    hoursTaught: 'จำนวนชั่วโมง',
    studentsPresent: 'จำนวนน้องที่มาเรียน',
    studentsAbsent: 'จำนวนน้องที่ขาดเรียน',
    remark: 'หมายเหตุ',
    formType: 'ประเภทฟอร์ม',
    classId: 'คลาสที่สอน',
    className: 'คลาสที่สอน'
  };
  return labels[fieldName] || fieldName;
};

const formatValue = (fieldName, value) => {
  if (!value || value === '') return '-';
  
  if (fieldName === 'hoursTaught' && !isNaN(value)) {
    return Number(value).toFixed(1);
  }
  
  if (fieldName === 'teachingDate') {
    try {
      const date = new Date(value);
      return date.toLocaleDateString('th-TH', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      });
    } catch (e) {
      return value;
    }
  }
  
  return value;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleString('th-TH', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateString;
  }
};
</script>