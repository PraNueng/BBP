<template>
  <div
    v-if="show"
    class="fixed inset-0 backdrop-blur-sm bg-black/20 flex items-center justify-center p-4 z-50"
    @click.self="$emit('close')"
  >
    <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[80vh] overflow-hidden flex flex-col">
      <!-- Header -->
      <div class="bg-gradient-to-r from-blue-600 to-indigo-600 p-6 flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-white">จัดการครูผู้สอน</h2>
          <p class="text-blue-100 mt-1">{{ getClassTitle() }}</p>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 hover:bg-white/20 rounded-lg transition"
        >
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-8">
          <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
          <p class="mt-2 text-gray-600">กำลังโหลด...</p>
        </div>

        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="mb-4 p-4 bg-green-50 border-l-4 border-green-500 text-green-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-green-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{{ errorMessage }}</span>
        </div>

        <div v-if="!isLoading">
          <!-- ครูที่สอนอยู่ -->
          <div class="mb-6">
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              ครูที่สอนอยู่ ({{ assignedTutors.length }} คน)
            </h3>
            
            <div v-if="assignedTutors.length === 0" class="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
              <svg class="mx-auto h-12 w-12 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <p>ยังไม่มีครูในคลาสนี้</p>
            </div>

            <div v-else class="space-y-2">
              <div
                v-for="tutor in assignedTutors"
                :key="tutor.id"
                class="flex items-center justify-between p-3 bg-blue-50 border border-blue-200 rounded-lg hover:bg-blue-100 transition"
              >
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-bold">
                    {{ getTutorInitial(tutor) }}
                  </div>
                  <div>
                    <p class="font-semibold text-gray-800">{{ tutor.nickname || tutor.username }}</p>
                    <p class="text-xs text-gray-500">@{{ tutor.username }}</p>
                  </div>
                </div>
                <button
                  @click="confirmRemoveTutor(tutor)"
                  :disabled="isSubmitting"
                  class="p-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
                  title="ลบครู"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- เพิ่มครูใหม่ -->
          <div>
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              เพิ่มครูใหม่
            </h3>

            <div class="mb-4">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="ค้นหาครู..."
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div v-if="filteredAvailableTutors.length === 0" class="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
              <p>{{ searchQuery ? 'ไม่พบครูที่ค้นหา' : 'ไม่มีครูที่สามารถเพิ่มได้' }}</p>
            </div>

            <div v-else class="space-y-2 max-h-64 overflow-y-auto">
              <div
                v-for="tutor in filteredAvailableTutors"
                :key="tutor.id"
                class="flex items-center justify-between p-3 bg-gray-50 border border-gray-200 rounded-lg hover:bg-gray-100 transition"
              >
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 bg-gray-600 rounded-full flex items-center justify-center text-white font-bold">
                    {{ getTutorInitial(tutor) }}
                  </div>
                  <div>
                    <p class="font-semibold text-gray-800">{{ tutor.nickname || tutor.username }}</p>
                    <p class="text-xs text-gray-500">@{{ tutor.username }}</p>
                  </div>
                </div>
                <button
                  @click="addTutor(tutor.id)"
                  :disabled="isSubmitting"
                  class="p-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
                  title="เพิ่มครู"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="border-t border-gray-200 p-4 bg-gray-50">
        <button
          @click="$emit('close')"
          class="w-full px-6 py-3 bg-gray-600 text-white font-bold rounded-lg hover:bg-gray-700 transition"
        >
          ปิด
        </button>
      </div>
    </div>
  </div>

  <!-- Confirm Dialog -->
  <ConfirmDialog
    :show="showConfirmDialog"
    :title="confirmDialog.title"
    :message="confirmDialog.message"
    :sub-message="confirmDialog.subMessage"
    :confirm-text="confirmDialog.confirmText"
    :cancel-text="confirmDialog.cancelText"
    :variant="confirmDialog.variant"
    @confirm="handleConfirmRemove"
    @cancel="closeConfirmDialog"
    @close="closeConfirmDialog"
    ref="confirmDialogRef"
  />
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import api from '@/api.js';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import SearchBar from '@/components/SearchBar.vue';

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
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

const emit = defineEmits(['close', 'success']);

const isLoading = ref(false);
const isSubmitting = ref(false);
const allTutors = ref([]);
const assignedTutors = ref([]);
const successMessage = ref('');
const errorMessage = ref('');
const searchQuery = ref('');

// Confirm Dialog State
const showConfirmDialog = ref(false);
const confirmDialogRef = ref(null);
const tutorToRemove = ref(null);
const confirmDialog = ref({
  title: '',
  message: '',
  subMessage: '',
  confirmText: 'ยืนยัน',
  cancelText: 'ยกเลิก',
  variant: 'danger'
});

// ครูที่ยังไม่ได้สอนคลาสนี้
const availableTutors = computed(() => {
  const assignedIds = new Set(assignedTutors.value.map(t => t.id));
  return allTutors.value.filter(t => !assignedIds.has(t.id));
});

// ครูที่กรองตามการค้นหา
const filteredAvailableTutors = computed(() => {
  if (!searchQuery.value.trim()) {
    return availableTutors.value;
  }
  const query = searchQuery.value.toLowerCase();
  return availableTutors.value.filter(t => 
    t.nickname?.toLowerCase().includes(query) ||
    t.username?.toLowerCase().includes(query)
  );
});

const getApiBasePath = () => {
  switch (props.classType) {
    case 'monthly':
      return '/monthly-classes';
    case 'hourly-group':
      return '/hourly-group-classes';
    case 'hourly-individual':
      return '/hourly-individual-classes';
    default:
      return '';
  }
};

const getClassTitle = () => {
  if (!props.classData) return '';
  
  if (props.classType === 'monthly' || props.classType === 'hourly-group') {
    return props.classData.className || `${props.classData.subjectName} - ${props.classData.subtypeName}`;
  } else if (props.classType === 'hourly-individual') {
    return props.classData.className || `${props.classData.subjectName} - ${props.classData.studentNickname}`;
  }
  return '';
};

const getTutorInitial = (tutor) => {
  const name = tutor.nickname || tutor.username;
  return name?.charAt(0)?.toUpperCase() || 'T';
};

const loadData = async () => {
  isLoading.value = true;
  clearMessages();
  
  try {
    
    // ดึงครูทั้งหมด
    const tutorsResponse = await api.get('/tutors', {
      params: { active: true }
    });
    allTutors.value = tutorsResponse.data;

    // ดึงครูที่สอนคลาสนี้
    const basePath = getApiBasePath();
    
    // ทั้ง monthly, hourly-group และ hourly-individual ใช้ endpoint เดียวกัน
    try {
      const classTutorsResponse = await api.get(`${basePath}/${props.classData.id}/tutors`);
      assignedTutors.value = classTutorsResponse.data || [];
    } catch (error) {
      console.error(`[ManageTutorsModal] Error fetching ${props.classType} tutors:`, error);
      assignedTutors.value = [];
    }

    // Fallback: some classes (created via student flows) may have the legacy single `tutor` field
    // on the class DTO (e.g., `tutorId`/`tutorName`) but no junction-table entries. Use that
    // single tutor as the assigned tutor if the tutors endpoint returned empty.
    try {
      if ((!assignedTutors.value || assignedTutors.value.length === 0) && props.classData) {
        const legacyTutorId = props.classData.tutorId || props.classData.tutor?.id || null;
        const legacyTutorName = props.classData.tutorName || props.classData.tutor?.nickname || props.classData.tutorNickname || null;
        if (legacyTutorId || legacyTutorName) {
          assignedTutors.value = [{ id: legacyTutorId, nickname: legacyTutorName }];
        }
      }
    } catch (e) {
      // no-op fallback
    }
  } catch (error) {
    console.error('[ManageTutorsModal] Error loading data:', error);
    showError('ไม่สามารถโหลดข้อมูลครูได้: ' + (error.response?.data?.message || error.message));
  } finally {
    isLoading.value = false;
  }
};

const isTutorAlreadyAssigned = (tutorId) => {
  return assignedTutors.value.some(t => t.id === tutorId);
};

const addTutor = async (tutorId) => {
  if (isTutorAlreadyAssigned(tutorId)) {
    const tutor = allTutors.value.find(t => t.id === tutorId);
    const tutorName = tutor?.nickname || tutor?.username || 'ครูท่านนี้';
    showError(`❌ ${tutorName} สอนคลาสนี้อยู่แล้ว ไม่สามารถเพิ่มซ้ำได้`);
    return;
  }

  isSubmitting.value = true;
  clearMessages();
  
  try {
    const basePath = getApiBasePath();
    
    // ทั้ง monthly, hourly-group และ hourly-individual ใช้ endpoint เดียวกัน
    if (props.classType === 'monthly' || props.classType === 'hourly-group' || props.classType === 'hourly-individual') {
      await api.post(`${basePath}/${props.classData.id}/tutors`, {
        tutorIds: [tutorId]
      });
    } else {
      await api.patch(`${basePath}/${props.classData.id}/assign-tutor/${tutorId}`);
    }
    
    const tutor = allTutors.value.find(t => t.id === tutorId);
    const tutorName = tutor?.nickname || tutor?.username || 'ครู';
    showSuccess(`เพิ่ม ${tutorName} สำเร็จ!`);
    emit('success', `เพิ่ม ${tutorName} สำเร็จ!`);
    await loadData();
  } catch (error) {
    console.error('[ManageTutorsModal] Error adding tutor:', error);
    const errorMsg = error.response?.data?.message || 'ไม่สามารถเพิ่มครูได้';
    showError(`❌ ${errorMsg}`);
  } finally {
    isSubmitting.value = false;
  }
};

// แสดง Confirm Dialog
const confirmRemoveTutor = (tutor) => {
  tutorToRemove.value = tutor;
  const tutorName = tutor.nickname || tutor.username;
  
  confirmDialog.value = {
    title: 'ยืนยันการลบครู',
    message: `ต้องการลบ "${tutorName}" ออกจากคลาสนี้หรือไม่?`,
    subMessage: 'การดำเนินการนี้จะลบครูออกจากคลาสทันที แต่ยังสามารถเพิ่มกลับเข้ามาได้ในภายหลัง',
    confirmText: 'ยืนยัน',
    cancelText: 'ยกเลิก',
    variant: 'danger'
  };
  
  showConfirmDialog.value = true;
};

// Handle Confirm Remove
const handleConfirmRemove = async () => {
  if (!tutorToRemove.value) {
    confirmDialogRef.value?.resetLoading();
    closeConfirmDialog();
    return;
  }

  const tutorId = tutorToRemove.value.id;
  const tutorName = tutorToRemove.value.nickname || tutorToRemove.value.username;
  
  try {
    const basePath = getApiBasePath();
    
    // ทั้ง monthly, hourly-group และ hourly-individual ใช้ endpoint เดียวกัน
    if (props.classType === 'monthly' || props.classType === 'hourly-group' || props.classType === 'hourly-individual') {
      await api.delete(`${basePath}/${props.classData.id}/tutors`, {
        data: { tutorIds: [tutorId] },
        headers: {
          'Content-Type': 'application/json'
        }
      });
    } else {
      await api.patch(`${basePath}/${props.classData.id}/remove-tutor/${tutorId}`);
    }
    
    showSuccess(`ลบ ${tutorName} สำเร็จ!`);
    emit('success', `ลบ ${tutorName} สำเร็จ!`);
    
    confirmDialogRef.value?.resetLoading();
    
    await loadData();
    closeConfirmDialog();
  } catch (error) {
    console.error('[ManageTutorsModal] Error removing tutor:', error);
    showError(`${error.response?.data?.message || 'ไม่สามารถลบครูได้'}`);
    
    confirmDialogRef.value?.resetLoading();
    closeConfirmDialog();
  }
};

// Close Confirm Dialog
const closeConfirmDialog = () => {
  showConfirmDialog.value = false;
  tutorToRemove.value = null;
};

const showSuccess = (message) => {
  successMessage.value = message;
  errorMessage.value = '';
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const showError = (message) => {
  errorMessage.value = message;
  successMessage.value = '';
  setTimeout(() => {
    errorMessage.value = '';
  }, 5000);
};

const clearMessages = () => {
  successMessage.value = '';
  errorMessage.value = '';
};

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadData();
    searchQuery.value = '';
    clearMessages();
  }
});

onMounted(() => {
  if (props.show) {
    loadData();
  }
});
</script>