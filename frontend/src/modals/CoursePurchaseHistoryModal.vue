<template>
  <div v-if="show" 
    class="fixed inset-0 backdrop-blur-sm bg-black/60 flex items-center justify-center z-[70] p-4"
    @click.self="$emit('close')"
  >
    <div class="bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 rounded-3xl shadow-2xl w-full max-w-4xl max-h-[85vh] overflow-hidden">
      
      <!-- Header -->
      <div class="bg-gradient-to-r from-purple-600 to-pink-700 p-6 rounded-t-3xl">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="p-2 bg-white/20 rounded-lg">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="text-2xl font-bold text-white">ประวัติการซื้อคอร์ส</h2>
          </div>
          <button 
            @click="$emit('close')" 
            class="text-white hover:bg-white/20 rounded-full p-2 transition"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-24">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-purple-400"></div>
      </div>

      <!-- Content -->
      <div v-else class="overflow-y-auto max-h-[calc(85vh-100px)] p-6 bg-white">
        <div v-if="history.length === 0" class="text-center py-12">
          <p class="text-gray-500 text-lg">ยังไม่มีประวัติการซื้อคอร์ส</p>
        </div>

        <div v-else class="space-y-4">
          <div v-for="(item, index) in history" :key="index" 
            class="p-5 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl border-2 border-purple-200 shadow-sm hover:shadow-md transition-all">
            
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span v-if="item.action === 'INSERT'" class="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full">เพิ่ม</span>
                  <span v-else-if="item.action === 'UPDATE'" class="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-bold rounded-full">แก้ไข</span>
                  <span v-else-if="item.action === 'DELETE'" class="px-3 py-1 bg-red-100 text-red-700 text-xs font-bold rounded-full">ลบ</span>
                  <span class="text-sm text-gray-600">{{ item.fieldName }}</span>
                </div>

                <div v-if="item.oldValue" class="mb-1">
                  <span class="text-xs text-gray-500">จาก:</span>
                  <span class="text-sm text-red-600 font-medium ml-2">{{ item.oldValue }}</span>
                </div>

                <div v-if="item.newValue">
                  <span class="text-xs text-gray-500">เป็น:</span>
                  <span class="text-sm text-green-600 font-medium ml-2">{{ item.newValue }}</span>
                </div>
              </div>

              <div class="text-right">
                <p class="text-xs text-gray-500">{{ formatDate(item.updatedAt) }}</p>
                <p class="text-xs text-gray-600 font-medium">โดย: {{ item.updatedBy }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import api from '@/api.js';

const props = defineProps({
  show: Boolean,
  studentId: Number
});

const emit = defineEmits(['close']);

const history = ref([]);
const loading = ref(false);

const loadHistory = async () => {
  if (!props.studentId) return;
  
  loading.value = true;
  try {
    const response = await api.get(`/students/${props.studentId}/history`);
    
    // กรองเฉพาะประวัติที่เกี่ยวกับคอร์ส
    history.value = response.data.filter(item => {
      const courseFields = ['coursePurchaseAdded', 'แก้ไขคอร์ส', 'ลบคอร์ส'];
      return courseFields.includes(item.fieldName);
    });
  } catch (error) {
    console.error('Error loading course history:', error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('th-TH', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadHistory();
  }
});
</script>