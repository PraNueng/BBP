
<template>
  <div class="bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all overflow-hidden border-2"
       :class="classData.isActive ? 'border-blue-300' : 'border-gray-300 opacity-75'">
    <div class="p-4" :class="headerClass">
      <div class="flex items-start justify-between mb-3">
        <div class="flex-1">
          <h4 class="text-lg font-bold text-white mb-1">{{ classData.className || 'ไม่มีชื่อ' }}</h4>
          <p class="text-sm text-white/80">{{ classData.subjectName || '-' }}</p>
          <p v-if="!readOnly && latestStatusText" class="text-xs mt-1 text-red-300 font-semibold">
            ล่าสุด: {{ latestStatusText }}
          </p>
        </div>
        
        <!-- Status Badge (เมื่อเป็น Read-only) -->
        <div v-if="readOnly">
          <span 
            class="px-4 py-2 rounded-full text-xs font-bold shadow-md"
            :class="classData.isActive 
              ? 'bg-green-500 text-white' 
              : 'bg-gray-500 text-white'"
          >
            {{ classData.isActive ? '✓ สอนอยู่' : '✕ ไม่สอนแล้ว' }}
          </span>
        </div>
        
        <!-- Toggle Button (เมื่อไม่ใช่ Read-only) -->
        <button 
          v-else
          @click="$emit('toggle', type, classData.id, classData.isActive, classData.enrollmentId)"
          class="relative inline-flex h-8 w-14 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2"
          :class="classData.isActive ? 'bg-green-500' : 'bg-gray-400'"
        >
          <span class="inline-block h-6 w-6 transform rounded-full bg-white transition-transform"
                :class="classData.isActive ? 'translate-x-7' : 'translate-x-1'">
          </span>
        </button>
      </div>
    </div>

    <div class="p-4 space-y-3">
      <!-- <div v-if="classData.tutorName" class="flex items-center gap-2 text-sm">
        <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
        </svg>
        <span class="text-gray-700"><span class="font-semibold">ครูผู้สอน:</span> พี่{{ classData.tutorName }}</span>
      </div> -->

      <!-- <div v-if="classData.subtypeName" class="flex items-center gap-2 text-sm">
        <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
        </svg>
        <span class="text-gray-700">กลุ่ม {{ classData.subtypeName }}</span>
      </div> -->

      <div v-if="type === 'monthly' && (classData.startDate || classData.endDate)" class="flex items-center gap-2 text-sm">
        <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
        </svg>
        <span class="text-gray-700">{{ formatDateRange(classData.startDate, classData.endDate) }}</span>
      </div>

      <div v-if="type !== 'monthly' && classData.hoursTarget" class="flex items-center gap-2 text-sm">
        <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        <span class="text-gray-700">
          <span class="font-semibold">เป้าหมาย:</span> {{ classData.hoursTarget }} ชั่วโมง
        </span>
      </div>

      <div v-if="(type === 'hourly-group' || type === 'hourly-individual') && classData.hoursTarget && classData.hoursTarget > 0">
        <div class="flex justify-between text-sm mb-1">
          <span class="text-gray-600">ความคืบหน้า</span>
          <span class="font-semibold text-blue-600">
            {{ classData.hoursCompleted || 0 }}/{{ classData.hoursTarget }} ชม.
          </span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2">
          <div class="bg-blue-600 h-2 rounded-full transition-all" 
              :style="{ width: progressPercentage + '%' }">
          </div>
        </div>
      </div>

      <!-- ปุ่มประวัติ -->
      <button
        v-if="!readOnly"
        @click="toggleHistory"
        class="w-full mt-2 px-3 py-2 text-sm bg-gray-100 hover:bg-gray-200 rounded-lg transition flex items-center justify-center gap-2"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ showHistory ? 'ซ่อนประวัติ' : 'ดูประวัติ' }}
      </button>
      
      <!-- History Dropdown -->
      <div v-if="showHistory" class="mt-2 border-t pt-3">
        <div v-if="isLoadingHistory" class="text-center py-2 text-gray-500">
          กำลังโหลด...
        </div>
        <div v-else-if="history.length === 0" class="text-center py-2 text-gray-500 text-sm">
          ไม่มีประวัติ
        </div>
        <div v-else class="space-y-2 max-h-48 overflow-y-auto">
          <div 
            v-for="item in history" 
            :key="item.id"
            class="text-xs p-2 bg-gray-50 rounded"
          >
            <div class="flex items-center justify-between mb-1">
              <span :class="item.isActive ? 'text-green-600' : 'text-red-600'" class="font-semibold">
                {{ item.isActive ? '✓ เปิด' : '✕ ปิด' }}
              </span>
              <span class="text-gray-500">{{ formatDate(item.statusChangedAt) }}</span>
            </div>
            <div v-if="item.statusChangeReason" class="text-gray-600">
              หมายเหตุ: {{ item.statusChangeReason }}
            </div>
            <div class="text-gray-500">
              โดย: {{ item.changedByNickname }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import api from '@/api.js';

const showHistory = ref(false);
const history = ref([]);
const isLoadingHistory = ref(false);
const hasLoadedOnce = ref(false);

const fetchHistory = async () => {
  if (!props.classData.id) return;
  
  isLoadingHistory.value = true;
  try {
    const enrollmentId = props.classData.enrollmentId || props.classData.id;

    if (!enrollmentId) {
      console.error('No enrollment ID available');
      return;
    }

    const response = await api.get(
      `/enrollments/${props.type}/${enrollmentId}/history`
    );
    history.value = response.data;
  } catch (err) {
    console.error('Error fetching history:', err);
  } finally {
    isLoadingHistory.value = false;
    hasLoadedOnce.value = true;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('th-TH', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const latestStatusText = computed(() => {
  // ถ้ายังไม่เคยโหลด และไม่ได้กำลังโหลดอยู่ ให้โหลด
  if (!hasLoadedOnce.value && !isLoadingHistory.value) {
    fetchHistory();
    return null;
  }
  
  // ใช้ข้อมูลจาก history[0] (ล่าสุด)
  if (history.value.length > 0) {
    const latest = history.value[0];
    const statusText = latest.isActive ? 'เปิดใช้งาน' : 'ปิดใช้งาน';
    const dateText = formatDate(latest.statusChangedAt);
    
    return `${statusText} (${dateText})`;
  }
  
  return null;
});

const toggleHistory = () => {
  showHistory.value = !showHistory.value;
  if (showHistory.value && history.value.length === 0) {
    fetchHistory();
  }
};

const props = defineProps({
  classData: Object,
  type: String,
  readOnly: {
    type: Boolean,
    default: false
  }
});

defineEmits(['toggle']);

const headerClass = computed(() => {
  if (props.type === 'monthly') return 'bg-gradient-to-r from-purple-500 to-purple-600';
  if (props.type === 'hourly-group') return 'bg-gradient-to-r from-blue-500 to-blue-600';
  return 'bg-gradient-to-r from-green-500 to-green-600';
});

const progressPercentage = computed(() => {
  if (!props.classData.hoursTarget || props.classData.hoursTarget === 0) return 0;
  const completed = props.classData.hoursCompleted || 0;
  return Math.min(Math.round((completed / props.classData.hoursTarget) * 100), 100);
});

const formatDateRange = (start, end) => {
  if (!start && !end) return '-';
  const formatDate = (date) => {
    if (!date) return '';
    return new Date(date).toLocaleDateString('th-TH', { year: 'numeric', month: 'short', day: 'numeric' });
  };
  return `${formatDate(start)} - ${formatDate(end)}`;
};
</script>