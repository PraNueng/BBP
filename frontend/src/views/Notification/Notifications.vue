<template>
  <NavBar />
  
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
    <!-- Animated Background -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-blue-500/10 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
    </div>

    <div class="relative z-10 container mx-auto px-4 py-4">
    <!-- Top Row: BackButton + Filter Buttons -->
    <div class="flex items-center justify-between mb-6">
      <BackButton to="/home-admin" />
      
      <!-- Filter Buttons ชิดขวา -->
      <div class="flex gap-3">
        <button
          v-for="filter in filters"
          :key="filter.value"
          @click="selectedFilter = filter.value"
          class="px-5 py-2.5 rounded-xl font-semibold transition-all shadow-md hover:shadow-lg"
          :class="selectedFilter === filter.value
            ? 'bg-blue-600 text-white'
            : 'bg-slate-800/40 text-gray-300 hover:bg-slate-700/40 border border-slate-600/50'"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- Main Header Row -->
    <div class="mb-8 grid grid-cols-12 gap-4 items-center">
      <!-- Stats Cards - ชิดซ้าย (3 columns) -->
      <div class="col-span-12 lg:col-span-4 flex gap-3">
        <div class="bg-slate-800/40 backdrop-blur-xl rounded-2xl p-4 border border-slate-400/50 w-40">
          <div class="flex flex-col items-center">
            <div class="w-10 h-10 bg-blue-500/20 rounded-full flex items-center justify-center mb-2">
              <svg class="w-5 h-5 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
              </svg>
            </div>
            <p class="text-gray-400 text-xs mb-1">ทั้งหมด</p>
            <p class="text-2xl font-bold text-white">{{ notifications.length }}</p>
          </div>
        </div>

        <div class="bg-slate-800/40 backdrop-blur-xl rounded-2xl p-4 border border-yellow-500/50 w-40">
          <div class="flex flex-col items-center">
            <div class="w-10 h-10 bg-yellow-500/20 rounded-full flex items-center justify-center mb-2">
              <svg class="w-5 h-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <p class="text-gray-400 text-xs mb-1">ยังไม่อ่าน</p>
            <p class="text-2xl font-bold text-yellow-400">{{ unreadCount }}</p>
          </div>
        </div>

        <div class="bg-slate-800/40 backdrop-blur-xl rounded-2xl p-4 border border-green-500/50 w-40">
          <div class="flex flex-col items-center">
            <div class="w-10 h-10 bg-green-500/20 rounded-full flex items-center justify-center mb-2">
              <svg class="w-5 h-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <p class="text-gray-400 text-xs mb-1">อ่านแล้ว</p>
            <p class="text-2xl font-bold text-green-400">{{ readCount }}</p>
          </div>
        </div>
      </div>

      <!-- Title + Icon - กึ่งกลาง (4 columns) -->
      <div class="col-span-12 lg:col-span-4 flex flex-col items-center justify-center text-center">
        <div class="flex items-center gap-3 mb-2">
          <svg class="w-10 h-10 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
          </svg>
          <h1 class="text-5xl font-bold text-white">การแจ้งเตือน</h1>
        </div>
        <p class="text-gray-400 text-md">ติดตามนักเรียนที่เรียนครบชั่วโมงตามที่ซื้อมาแล้ว</p>
      </div>

      <!-- Search + Dropdowns - ชิดขวา (4 columns) -->
      <div class="col-span-12 lg:col-span-4 flex flex-col gap-3 items-end">
        <div class="flex gap-2.5">
          <SimpleSelect
            v-model="selectedClassType"
            :options="classTypes"
            placeholder="ประเภทคลาส"
            type="classType"
            class="max-w-40"
          />

          <SimpleSelect
            v-model="selectedGrade"
            :options="grades"
            placeholder="ชั้น"
            type="grade"
            class="max-w-20"
          />

          <SchoolInputWithDropdown
            v-model="selectedSchool"
            :options="schools"
            placeholder="โรงเรียน"
            type="school"
            class="max-w-40"
          />
          
          <SimpleSelect
            v-model="selectedSubject"
            :options="subjects"
            placeholder="วิชา"
            type="subject"
            class="max-w-32"
          />
        </div>
        
        <div class="flex flex-row gap-4">
          <SearchBar
            v-model="searchKeyword"
            placeholder="ค้นหา"
            class="max-w-110 text-white"
          />

          <!-- ปุ่มสลับการเรียงลำดับวันที่แจ้ง notification -->
          <button
            @click="sortOrder = sortOrder === 'desc' ? 'asc' : 'desc'"
            class="px-3 py-2 max-w-35 rounded-xl bg-gradient-to-br from-yellow-500 to-yellow-800 text-white font-semibold shadow-lg border-2 border-gray-300 hover:border-indigo-400 hover:shadow-xl focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-indigo-500 transition-all duration-300 flex items-center gap-2 text-sm"
          >
            <svg class="w-6 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path v-if="sortOrder === 'desc'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h9m5-4v12m0 0l-4-4m4 4l4-4"/>
              <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12"/>
            </svg>
            <span class="w-12">{{ sortOrder === 'desc' ? 'ล่าสุด' : 'เก่าสุด' }}</span>
          </button>

          <!-- ปุ่มสลับการเรียงลำดับรหัสใบเสร็จ -->
          <button
            @click="toggleReceiptSort"
            class="px-3 py-2 rounded-xl font-semibold shadow-lg border-2 transition-all duration-300 flex items-center gap-2 text-sm"
            :class="receiptSortOrder !== 'none' 
              ? 'bg-gradient-to-br from-purple-500 to-purple-800 text-white border-purple-300' 
              : 'bg-slate-800/40 text-gray-300 border-slate-600/50 hover:bg-slate-700/40'"
          >
            <svg class="w-5 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path v-if="receiptSortOrder === 'asc'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h9m5-4v12m0 0l-4-4m4 4l4-4"/>
              <path v-else-if="receiptSortOrder === 'desc'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12"/>
              <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4"/>
            </svg>
            <span class="w-20">
              {{ receiptSortOrder === 'asc' ? 'น้อย → มาก' : receiptSortOrder === 'desc' ? 'มาก → น้อย' : 'POS' }}
            </span>
          </button>

          <!-- ปุ่ม Clear -->
          <button
            v-if="hasActiveFilters"
            @click="clearAllFilters"
            class="px-4 py-2 rounded-xl bg-red-500/20 border border-red-400/40 text-red-300 font-semibold hover:bg-red-500/30 hover:text-white transition-all duration-300 flex items-center gap-2 text-sm"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M6 18L18 6M6 6l12 12" />
            </svg>
            ล้าง
          </button>
        </div>
      </div>
    </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex items-center justify-center py-24">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-blue-400 mb-4"></div>
          <p class="text-gray-300 text-lg">กำลังโหลดการแจ้งเตือน...</p>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-500/20 border border-red-500 rounded-2xl p-6">
        <div class="flex items-center gap-3">
          <svg class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <p class="text-red-400 font-semibold">{{ error }}</p>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredNotifications.length === 0" class="bg-slate-800/30 backdrop-blur-sm rounded-2xl p-12 text-center border border-slate-700/50">
        <svg class="w-20 h-20 text-gray-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
        </svg>
        <p class="text-gray-400 text-xl font-semibold">ไม่มีการแจ้งเตือน</p>
        <p class="text-gray-500 text-sm mt-2">เมื่อมีนักเรียนเรียนครบชั่วโมงจะแสดงที่นี่</p>
      </div>

      <!-- Notifications List -->
      <div v-else class="space-y-4">
        <transition-group name="notification">
          <NotificationCard
            v-for="notification in filteredNotifications"
            :key="notification.id"
            :notification="notification"
            @mark-read-with-receipt="handleMarkReadWithReceipt"
            @edit-receipt="handleEditReceipt"
            @view-student="viewStudent"
            @open-detail-modal="openStudentDetailModal"
          />
        </transition-group>

        <!-- Modal สำหรับ Mark as Read (กดติ๊กถูก) -->
        <ReceiptCodeModal
          :show="showReceiptModal"
          :initialValue="''"
          @confirm="submitReceiptCode"
          @cancel="cancelReceiptModal"
        />

        <!-- Modal สำหรับ Edit Receipt (กดปุ่มแก้ไข) -->
        <ReceiptCodeModal
          :show="showEditReceiptModal"
          :initialValue="editingReceiptCode"
          @confirm="submitEditReceiptCode"
          @cancel="cancelEditReceipt"
        />
      </div>

      <!-- Success Message -->
      <div v-if="successMessage" class="fixed bottom-6 right-6 bg-green-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 animate-bounce z-50">
        <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
        </svg>
        <span class="font-semibold">{{ successMessage }}</span>
      </div>
    </div>

    <StudentDetail
      :show="showStudentDetailModal"
      :studentId="selectedStudentIdForDetail"
      @close="closeStudentDetailModal"
      @edit="handleEditFromDetail"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import NotificationCard from '@/views/Notification/NotificationCard.vue';
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import StudentDetail from '@/modals/StudentDetail.vue';
import ReceiptCodeModal from '@/views/Notification/ReceiptCodeModal.vue';
import SearchBar from '@/components/SearchBar.vue';
import SearchableSelect from '@/components/SearchableSelect.vue';
import SimpleSelect from '@/components/SimpleSelect.vue';
import SchoolInputWithDropdown from '@/components/SchoolInputWithDropdown.vue';
import api from '@/api.js';

const router = useRouter();

const notifications = ref([]);
const isLoading = ref(false);
const error = ref('');
const successMessage = ref('');
const selectedFilter = ref('all');
const isMarkingAllRead = ref(false);
const showStudentDetailModal = ref(false);
const selectedStudentIdForDetail = ref(null);
const showReceiptModal = ref(false);
const selectedNotificationId = ref(null);
const searchKeyword = ref('');
const selectedClassType = ref('');
const selectedSubject = ref('');
const allSubjects = ref([]);
const showEditReceiptModal = ref(false);
const editingNotificationId = ref(null);
const editingReceiptCode = ref('');
const sortOrder = ref('desc');
const selectedSchool = ref('');
const allSchools = ref([]);
const selectedGrade = ref('');
const receiptSortOrder = ref('none');

const schools = computed(() => {
  const schoolSet = new Set();
  notifications.value.forEach(n => {
    if (n.schoolName) schoolSet.add(n.schoolName);
  });
  return Array.from(schoolSet).sort();
});

const filters = [
  { label: 'ทั้งหมด', value: 'all' },
  { label: 'ยังไม่อ่าน', value: 'unread' },
  { label: 'อ่านแล้ว', value: 'read' }
];

const classTypes = ref(['กลุ่มรวม', 'PV-เดี่ยว', 'PV-กลุ่ม', 'รายเดือน']);

const subjects = computed(() => {
  return allSubjects.value.map(s => s.subjectName).sort();
});

const clearAllFilters = () => {
  selectedClassType.value = '';
  selectedSubject.value = '';
  selectedSchool.value = '';
  selectedGrade.value = '';
  searchKeyword.value = '';

  // reset sort เป็น "ล่าสุด"
  sortOrder.value = 'desc';

  // (optional) reset filter read/unread ถ้าต้องการ
  selectedFilter.value = 'all';

  receiptSortOrder.value = 'none';
};

const hasActiveFilters = computed(() => {
  return (
    selectedClassType.value ||
    selectedSubject.value ||
    selectedSchool.value ||
    selectedGrade.value ||
    searchKeyword.value ||
    sortOrder.value !== 'desc' ||
    selectedFilter.value !== 'all'|| 
    receiptSortOrder.value !== 'none'
  );
});

const filteredNotifications = computed(() => {
  let filtered = notifications.value;
  
  // Filter by read status
  if (selectedFilter.value === 'unread') {
    filtered = filtered.filter(n => !n.isRead);
  } else if (selectedFilter.value === 'read') {
    filtered = filtered.filter(n => n.isRead);
  }
  
  // Filter by search keyword
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (keyword) {
    filtered = filtered.filter(n => 
      (n.studentName || '').toLowerCase().includes(keyword) ||
      (n.studentNickname || '').toLowerCase().includes(keyword) ||
      (n.studentCode || '').toLowerCase().includes(keyword) ||
      (n.receiptCode || '').toLowerCase().includes(keyword)
    );
  }
  
  // Filter by class type
  if (selectedClassType.value) {
    filtered = filtered.filter(n => {
      const ct = n.classType;
      if (selectedClassType.value === 'กลุ่มรวม') return ct === 'hourly_group';
      if (selectedClassType.value === 'PV-เดี่ยว') {
        return (ct === 'hourly_individual' && n.isIndividual === true);
      }
      if (selectedClassType.value === 'PV-กลุ่ม') {
        return (ct === 'hourly_individual' && n.isIndividual === false) || 
              ct === 'INDIVIDUAL_GROUP';
      }
      if (selectedClassType.value === 'รายเดือน') return ct === 'monthly';
      return true;
    });
  }
  
  // Filter by subject
  if (selectedSubject.value) {
    filtered = filtered.filter(n => n.subjectName === selectedSubject.value);
  }

  // Filter by school
  if (selectedSchool.value) {
    filtered = filtered.filter(n => n.schoolName === selectedSchool.value);
  }

  // Filter by grade
  if (selectedGrade.value) {
  filtered = filtered.filter(n => n.gradeName === selectedGrade.value);
}
  
  return filtered.sort((a, b) => {
    // ถ้าเลือก sort ตามรหัสใบเสร็จ
    if (receiptSortOrder.value !== 'none') {
      const codeA = a.receiptCode || '';
      const codeB = b.receiptCode || '';
      
      if (receiptSortOrder.value === 'asc') {
        return codeA.localeCompare(codeB);
      } else {
        return codeB.localeCompare(codeA);
      }
    }
    
    // ถ้าไม่ได้เลือก sort ตามรหัสใบเสร็จ ใช้ sort ตามวันที่เดิม
    const dateA = new Date(a.createdAt);
    const dateB = new Date(b.createdAt);
    return sortOrder.value === 'desc' ? dateB - dateA : dateA - dateB;
  });
});

const grades = ref(['ม.1', 'ม.2', 'ม.3', 'ม.4', 'ม.5', 'ม.6', 'อื่น ๆ']);

const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length);
const readCount = computed(() => notifications.value.filter(n => n.isRead).length);

const loadNotifications = async () => {
  isLoading.value = true;
  error.value = '';
  
  try {
    const response = await api.get('/notifications');
    const notifs = response.data;
    
    // ดึงประวัติสำหรับที่อ่านแล้ว
    for (const n of notifs) {
      if (n.isRead && n.receiptCode) {
        try {
          const historyRes = await api.get(`/notifications/${n.id}/receipt-code-history`);
          if (historyRes.data && historyRes.data.length > 0) {
            n.historyRecords = historyRes.data;
            n.hasHistory = historyRes.data.length > 0;
          }
        } catch (err) {
          console.error(`Failed to load history for notification ${n.id}:`, err);
        }
      }
    }
    
    notifications.value = notifs;
  } catch (err) {
    console.error('Error loading notifications:', err);
    error.value = 'ไม่สามารถโหลดการแจ้งเตือนได้';
  } finally {
    isLoading.value = false;
  }
};

const markAsRead = async (notificationId) => {
  try {
    await api.patch(`/notifications/${notificationId}/mark-read`);
    
    const notification = notifications.value.find(n => n.id === notificationId);
    if (notification) {
      notification.isRead = true;
      notification.readAt = new Date().toISOString();
    }
    
    showSuccess('ทำเครื่องหมายว่าอ่านแล้ว');
  } catch (err) {
    console.error('Error marking notification as read:', err);
    error.value = 'ไม่สามารถทำเครื่องหมายว่าอ่านแล้วได้';
  }
};

const markAllAsRead = async () => {
  isMarkingAllRead.value = true;
  
  try {
    await api.patch('/notifications/mark-all-read');
    
    notifications.value.forEach(n => {
      n.isRead = true;
      n.readAt = new Date().toISOString();
    });
    
    showSuccess('ทำเครื่องหมายทั้งหมดว่าอ่านแล้ว');
  } catch (err) {
    console.error('Error marking all as read:', err);
    error.value = 'ไม่สามารถทำเครื่องหมายทั้งหมดได้';
  } finally {
    isMarkingAllRead.value = false;
  }
};

const viewStudent = (studentId) => {
  router.push(`/students/${studentId}`);
};

const showSuccess = (message) => {
  successMessage.value = message;
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const openStudentDetailModal = (studentId) => {
  selectedStudentIdForDetail.value = studentId;
  showStudentDetailModal.value = true;
};

const closeStudentDetailModal = () => {
  showStudentDetailModal.value = false;
  selectedStudentIdForDetail.value = null;
};

const handleEditFromDetail = (student) => {
  closeStudentDetailModal();
};

const handleMarkReadWithReceipt = (notificationId) => {
  selectedNotificationId.value = notificationId;
  showReceiptModal.value = true;
};

const toggleReceiptSort = () => {
  if (receiptSortOrder.value === 'none') {
    receiptSortOrder.value = 'asc';
  } else if (receiptSortOrder.value === 'asc') {
    receiptSortOrder.value = 'desc';
  } else {
    receiptSortOrder.value = 'none';
  }
};

const submitReceiptCode = async (receiptCode) => {
  try {
    // 1. อัปเดต receipt_code
    await api.patch(`/notifications/${selectedNotificationId.value}/receipt-code`, {
      receiptCode: receiptCode
    });
    
    // 2. mark as read
    await api.patch(`/notifications/${selectedNotificationId.value}/mark-read`);
    
    // 3. อัปเดต local state
    const notification = notifications.value.find(n => n.id === selectedNotificationId.value);
    if (notification) {
      notification.isRead = true;
      notification.receiptCode = receiptCode;
      notification.readAt = new Date().toISOString();
    }
    
    // 4. ปิด modal
    showReceiptModal.value = false;
    selectedNotificationId.value = null;
    
    // 5. แสดง success message
    showSuccess('บันทึกรหัสใบเสร็จและทำเครื่องหมายว่าอ่านแล้ว');
  } catch (err) {
    console.error('Error submitting receipt code:', err);
    error.value = 'ไม่สามารถบันทึกรหัสใบเสร็จได้';
    setTimeout(() => {
      error.value = '';
    }, 3000);
    throw err;
  }
};

const loadSubjects = async () => {
  try {
    const response = await api.get('/subjects/all');
    allSubjects.value = response.data.filter(s => s.isActive);
  } catch (error) {
    console.error('Error loading subjects:', error);
  }
};

const handleEditReceipt = (notificationId, currentReceiptCode) => {
  editingNotificationId.value = notificationId;
  editingReceiptCode.value = currentReceiptCode || '';
  showEditReceiptModal.value = true;
};

const submitEditReceiptCode = async (newReceiptCode) => {
  try {
    // 1. อัปเดต receipt code
    await api.patch(`/notifications/${editingNotificationId.value}/receipt-code`, {
      receiptCode: newReceiptCode
    });
    
    // 2. หา notification ที่กำลังแก้
    const notification = notifications.value.find(n => n.id === editingNotificationId.value);
    if (notification) {
      // อัปเดต receipt code ใหม่
      notification.receiptCode = newReceiptCode;
      
      // 3. ดึง history ใหม่จาก API
      try {
        const historyRes = await api.get(`/notifications/${editingNotificationId.value}/receipt-code-history`);
        if (historyRes.data && historyRes.data.length > 0) {
          notification.historyRecords = historyRes.data;
          notification.hasHistory = true;
        }
      } catch (err) {
        console.error('Failed to load updated history:', err);
      }
    }
    
    // 4. ปิด modal และแสดง success
    showEditReceiptModal.value = false;
    editingNotificationId.value = null;
    showSuccess('แก้ไขรหัสใบเสร็จสำเร็จ');
  } catch (err) {
    console.error('Error editing receipt code:', err);
    error.value = 'ไม่สามารถแก้ไขรหัสใบเสร็จได้';
    throw err;
  }
};

const cancelEditReceipt = () => {
  showEditReceiptModal.value = false;
  editingNotificationId.value = null;
  editingReceiptCode.value = '';
};

const cancelReceiptModal = () => {
  showReceiptModal.value = false;
  selectedNotificationId.value = null;
};

onMounted(() => {
  loadNotifications();
  loadSubjects();
});
</script>

<style scoped>
@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-bounce {
  animation: bounce 1s ease-in-out;
}

.animate-pulse {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.delay-1000 {
  animation-delay: 1s;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
}

.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.notification-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>