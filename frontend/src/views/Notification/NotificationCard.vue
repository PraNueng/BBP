<template>
  <div 
    class="bg-slate-800/40 backdrop-blur-xl rounded-2xl border transition-all hover:shadow-xl cursor-pointer overflow-hidden"
    :class="[
      notification.isRead 
        ? 'border-slate-700/50 opacity-75' 
        : 'border-yellow-500/50 shadow-lg shadow-yellow-500/10',
      notification.receiptCode === '000000' && notification.isRead 
        ? 'bg-red-900/20 border-red-500/50' 
        : ''
    ]"
  >
    <div class="p-6">
      <!-- Header -->
      <div class="flex items-start justify-between mb-4">
        <div class="flex items-start gap-4 flex-1">
          <!-- Icon -->
          <div 
            class="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0"
            :class="notification.isRead ? 'bg-gray-600/30' : 'bg-yellow-500/20 animate-pulse'"
          >
            <svg 
              class="w-6 h-6"
              :class="notification.isRead ? 'text-gray-400' : 'text-yellow-400'"
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>

          <!-- Content -->
          <div class="flex-1 min-w-0">
            <!-- Title -->
            <div class="flex items-center gap-3 mb-2">
              <h3 class="text-lg font-bold text-white">
                {{ notification.title || 'แจ้งเตือน' }}
              </h3>
              <span 
                v-if="!notification.isRead"
                class="px-2 py-0.5 bg-yellow-500 text-yellow-900 text-xs font-bold rounded-full"
              >
                ใหม่
              </span>
            </div>

            <!-- Message -->
            <p class="text-gray-300 mb-3">{{ notification.message }}</p>

            <!-- Student Info -->
            <div class="space-y-2">
              <div class="flex items-center gap-2 text-sm">
                <svg class="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                </svg>
                <span class="text-gray-300">
                  <span class="font-semibold text-white">{{ notification.studentNickname || notification.studentName }}</span>
                  <span class="text-gray-400 ml-1">({{ notification.studentCode }})</span>
                </span>
              </div>

              <div v-if="notification.gradeName" class="flex items-center gap-2 text-sm">
                <svg class="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                </svg>
                <span class="text-gray-300">{{ notification.gradeName }}</span>
              </div>

              <div v-if="notification.className" class="flex items-center gap-2 text-sm">
                <svg class="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                </svg>
                <span class="text-gray-300">{{ formatClassType(notification.classType) }}</span>
              </div>

              <div class="flex items-center gap-2 text-sm">
                <svg class="w-4 h-4 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
                </svg>
                <span class="text-gray-300">{{ getClassTypeLabel(notification) }}</span>
              </div>
            </div>

            <div v-if="notification.isRead && notification.receiptCode" class="flex items-center gap-2 text-sm">
              <svg class="w-4 h-4 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              <span class="text-gray-300">รหัสใบเสร็จ: </span>
              <span class="font-semibold text-white">{{ notification.receiptCode }}</span>
            </div>

            <!-- History (ถ้ามี) -->
            <div
              v-if="notification.isRead && notification.hasHistory && validHistoryRecords(notification).length > 0"
              class="flex items-start gap-2 text-xs text-gray-400 italic mt-1"
            >
              <!-- icon -->
              <svg class="w-3 h-3 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>

              <!-- content -->
              <div class="flex flex-row gap-1">
                <span class="font-semibold">ประวัติการแก้ไข:</span>

                <span
                  v-for="(record, index) in validHistoryRecords(notification)"
                  :key="index"
                  class="pl-4"
                >
                  <span>
                    แก้จาก {{ record.oldValue || '(ไม่มี)' }} เป็น {{ record.newValue }}
                    เมื่อ {{ formatHistoryDate(record.updatedAt) }}
                  </span>
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex items-start gap-2">
          <!-- Mark as Read Button -->
          <button
            v-if="!notification.isRead"
            @click.stop="$emit('mark-read-with-receipt', notification.id)"
            class="p-2 hover:bg-green-500/20 rounded-lg transition-all group"
            title="ทำเครื่องหมายว่าอ่านแล้วและกรอกรหัสใบเสร็จ"
          >
            <svg class="w-5 h-5 text-gray-400 group-hover:text-green-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
            </svg>
          </button>

          <!-- Edit Receipt Code Button (แสดงเฉพาะที่อ่านแล้ว) -->
          <button
            v-if="notification.isRead"
            @click.stop="$emit('edit-receipt', notification.id, notification.receiptCode)"
            class="p-2 hover:bg-yellow-500/20 rounded-lg transition-all group"
            title="แก้ไขรหัสใบเสร็จ"
          >
            <svg class="w-5 h-5 text-gray-400 group-hover:text-yellow-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
            </svg>
          </button>

          <!-- View Student Button -->
          <button
            @click.stop="$emit('open-detail-modal', notification.studentId)"
            class="p-2 hover:bg-blue-500/20 rounded-lg transition-all group"
            title="ดูข้อมูลนักเรียน"
          >
            <svg class="w-5 h-5 text-gray-400 group-hover:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Footer -->
      <!-- <div class="flex items-center justify-between pt-4 border-t border-slate-700/50">
        <div class="flex items-center gap-2 text-xs text-gray-400">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <span>{{ notification.timeAgo || formatTimeAgo(notification.createdAt) }}</span>
        </div>

        <div v-if="notification.isRead && notification.readByName" class="flex items-center gap-2 text-xs text-gray-400">
          <svg class="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
          </svg>
          <span>อ่านโดย {{ notification.readByName }}</span>
        </div>
      </div> -->
    </div>

    <!-- Milestone Badge -->
    <div 
      class="px-6 py-3 border-t"
      :class="getFooterBadgeClass(notification)"
    >
      <div class="flex items-center justify-between text-sm">
        
        <div class="flex items-center gap-2">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>

          <span class="text-yellow-300 font-semibold">
            {{ notification.timeAgo || formatTimeAgo(notification.createdAt) }}
          </span>
        </div>

        <div
          v-if="notification.isRead && notification.readByName"
          class="flex items-center gap-2 text-xs text-gray-400"
        >
          <svg class="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M5 13l4 4L19 7" />
          </svg>
          <span>อ่านโดย {{ notification.readByName }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  notification: {
    type: Object,
    required: true
  }
});

defineEmits(['mark-read-with-receipt', 'edit-receipt', 'view-student', 'open-detail-modal']);

const formatTimeAgo = (dateString) => {
  if (!dateString) return '';
  
  const now = new Date();
  const date = new Date(dateString);
  const diffMs = now - date;
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  
  if (diffHours < 1) {
    return 'เมื่อสักครู่';
  } else if (diffHours < 24) {
    return `${diffHours} ชั่วโมงที่แล้ว`;
  } else {
    const diffDays = Math.floor(diffHours / 24);
    if (diffDays === 1) {
      return 'เมื่อวาน';
    } else if (diffDays < 7) {
      return `${diffDays} วันที่แล้ว`;
    } else if (diffDays < 30) {
      const diffWeeks = Math.floor(diffDays / 7);
      return `${diffWeeks} สัปดาห์ที่แล้ว`;
    } else {
      const diffMonths = Math.floor(diffDays / 30);
      return `${diffMonths} เดือนที่แล้ว`;
    }
  }
};

const formatHistoryDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('th-TH', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric',
    hour: '2-digit', 
    minute: '2-digit' 
  });
};

const validHistoryRecords = (notification) => {
  if (!notification.historyRecords) return [];
  
  // กรองเฉพาะ history ที่ oldValue ไม่ใช่ "" หรือ null
  return notification.historyRecords.filter(record => {
    return record.oldValue && record.oldValue.trim() !== '';
  });
};

const getFooterBadgeClass = (notification) => {
  if (notification.notificationType === 'MONTHLY_EXPIRATION') {
    return 'bg-gradient-to-r from-red-600/30 to-pink-600/30 border-red-500/30';
  }
  
  return 'bg-gradient-to-r from-yellow-600/30 to-orange-600/30 border-yellow-500/30';
};

const getClassTypeLabel = (notification) => {
  const ct = notification.classType;
  
  if (ct === 'hourly_group') {
    return 'กลุ่มรวม';
  } else if (ct === 'hourly_individual' || ct === 'INDIVIDUAL_GROUP') {
    if (notification.isIndividual === true) {
      return 'PV-เดี่ยว';
    } else if (notification.isIndividual === false) {
      return 'PV-กลุ่ม';
    } else {
      return 'PV-กลุ่ม';
    }
  } else if (ct === 'monthly') {
    return 'รายเดือน';
  }
  
  return ct;
};

const formatClassType = (classType) => {
  if (!classType) return '';
  
  const ct = classType.toUpperCase();
  
  if (ct === 'INDIVIDUAL_GROUP') {
    return 'PV-กลุ่ม';
  }
  
  return classType;
};

const formatClassName = (className) => {
  if (!className) return '';
  
  // แปลงชื่อพิเศษ (case-insensitive)
  if (className.toLowerCase().includes('individual_group')) {
    return 'PV-กลุ่ม';
  }
  
  return className;
};
</script>