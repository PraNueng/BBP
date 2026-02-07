<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-amber-900 to-yellow-900">
    <NavBar />
    <BackButton to="/class-management-total" class="pt-8" />

    <div class="container mx-auto px-4 lg:px-8 py-2 max-w-7xl">
      <!-- Header -->
      <div class="bg-gradient-to-r from-yellow-800 to-amber-500 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          เพิ่มข้อมูลวิชา/ประเภทคลาสใหม่ในระบบ
        </h1>
        <!-- <p class="text-purple-50 text-center mt-3 text-lg">
          เพิ่มวิชาหรือประเภทคลาสใหม่เข้าระบบ
        </p> -->
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- Tabs -->
        <div class="flex gap-4 mb-8 pl-4 overflow-x-auto pb-2">
          <button
            @click="activeTab = 'subjects'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'subjects'
                ? 'bg-gradient-to-r from-pink-500 to-pink-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            วิชา
          </button>
          <button
            @click="activeTab = 'monthly'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'monthly'
                ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            ประเภทคลาสรายเดือน
          </button>
          <button
            @click="activeTab = 'hourly'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly'
                ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            ประเภทคลาสกลุ่มรวม
          </button>
        </div>

        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="mb-6 p-5 bg-gradient-to-r from-green-50 to-emerald-50 border-l-4 border-green-500 text-green-800 rounded-lg shadow-md flex items-center gap-3">
          <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="mb-6 p-5 bg-gradient-to-r from-red-50 to-pink-50 border-l-4 border-red-500 text-red-800 rounded-lg shadow-md flex items-center gap-3">
          <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ errorMessage }}</span>
        </div>

        <!-- Add New Item Form -->
        <div class="mb-8 p-6 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl border-2 border-blue-300 shadow-md">
          <h2 class="text-2xl font-bold text-blue-800 mb-4 flex items-center gap-2">
            <svg class="h-7 w-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            เพิ่ม{{ getTabLabel() }}
          </h2>
          <form @submit.prevent="handleAddItem" class="flex gap-4">
            <input
              v-model="newItemName"
              type="text"
              required
              :placeholder="'กรอกชื่อ' + getTabLabel() + 'ใหม่'"
              class="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-bold rounded-lg hover:from-green-600 hover:to-green-700 transition-all shadow-lg disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed flex items-center gap-2"
            >
              <svg v-if="!isSubmitting" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              <svg v-else class="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isSubmitting ? 'กำลังเพิ่ม...' : 'เพิ่ม' }}
            </button>
          </form>
        </div>

        <!-- Items List -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>

        <div v-else class="space-y-3">
                    <div
            v-for="item in getCurrentItems()"
            :key="item.id"
            :class="[
              'border-2 rounded-xl p-4 transition-all shadow-sm',
              item.isActive
                ? 'bg-white border-gray-200 hover:border-blue-300 hover:shadow-md'
                : 'bg-gray-100 border-gray-300 opacity-60'
            ]"
          >
            <div class="flex items-center justify-between gap-4">
              <!-- Display/Edit Mode -->
              <div class="flex-1 flex items-center gap-3">
                <div
                  class="w-12 h-12 rounded-full flex items-center justify-center text-white font-bold text-lg"
                  :class="item.isActive ? 'bg-gradient-to-r from-green-400 to-green-500' : 'bg-gray-400'"
                >
                  {{ item.displayOrder || '#' }}
                </div>
                
                <div class="flex-1">
                  <input
                    v-if="editingId === item.id"
                    v-model="editValue"
                    type="text"
                    class="w-full px-4 py-2 border-2 border-blue-400 rounded-lg focus:ring-2 focus:ring-blue-500 font-semibold"
                    @keyup.enter="handleSaveEdit(item.id)"
                    @keyup.esc="cancelEdit"
                  />
                  <div v-else>
                    <p class="text-lg font-semibold text-gray-800">{{ item.subjectName || item.subtypeName }}</p>
                    <p class="text-sm text-gray-500">
                      สถานะ: 
                      <span :class="item.isActive ? 'text-green-600 font-medium' : 'text-red-600 font-medium'">
                        {{ item.isActive ? 'ใช้งาน' : 'ปิดใช้งาน' }}
                      </span>
                    </p>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="flex gap-2">
                <template v-if="editingId === item.id">
                  <button
                    @click="handleSaveEdit(item.id)"
                    class="p-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
                    title="บันทึก"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </button>
                  <button
                    @click="cancelEdit"
                    class="p-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition"
                    title="ยกเลิก"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </template>
                <template v-else>
                  <button
                    @click="startEdit(item)"
                    class="p-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition"
                    title="แก้ไข"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button
                    @click="handleToggleActive(item)"
                    :class="[
                      'p-2 rounded-lg transition',
                      item.isActive
                        ? 'bg-orange-500 text-white hover:bg-orange-600'
                        : 'bg-green-500 text-white hover:bg-green-600'
                    ]"
                    :title="item.isActive ? 'ปิดใช้งาน' : 'เปิดใช้งาน'"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                    </svg>
                  </button>
                </template>
              </div>
            </div>
          </div>

          <div v-if="getCurrentItems().length === 0" class="text-center py-12 text-gray-500">
            <p class="text-lg">ยังไม่มีข้อมูล{{ getTabLabel() }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import api from '@/api.js';

const activeTab = ref('subjects');
const subjects = ref([]);
const monthlySubtypes = ref([]);
const hourlyGroupSubtypes = ref([]);
const isLoading = ref(false);
const isSubmitting = ref(false);
const editingId = ref(null);
const editValue = ref('');
const newItemName = ref('');
const successMessage = ref('');
const errorMessage = ref('');

// Load data based on active tab
const loadData = async () => {
  isLoading.value = true;
  try {
    if (activeTab.value === 'subjects') {
      const response = await api.get('/subjects/all');
      subjects.value = response.data;
    } else if (activeTab.value === 'monthly') {
      const response = await api.get('/monthly-subtypes/all');
      monthlySubtypes.value = response.data;
    } else if (activeTab.value === 'hourly') {
      const response = await api.get('/hourly-group-subtypes/all');
      hourlyGroupSubtypes.value = response.data;
    }
  } catch (error) {
    console.error('Error loading data:', error);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลได้';
    setTimeout(() => errorMessage.value = '', 3000);
  } finally {
    isLoading.value = false;
  }
};

// Get current items based on tab
const getCurrentItems = () => {
  if (activeTab.value === 'subjects') return subjects.value;
  if (activeTab.value === 'monthly') return monthlySubtypes.value;
  if (activeTab.value === 'hourly') return hourlyGroupSubtypes.value;
  return [];
};

// Get tab label
const getTabLabel = () => {
  if (activeTab.value === 'subjects') return 'วิชา';
  if (activeTab.value === 'monthly') return 'ประเภทคลาสรายเดือน';
  if (activeTab.value === 'hourly') return 'ประเภทคลาสกลุ่มรวม';
  return '';
};

// Get API endpoint
const getEndpoint = () => {
  if (activeTab.value === 'subjects') return '/subjects';
  if (activeTab.value === 'monthly') return '/monthly-subtypes';
  if (activeTab.value === 'hourly') return '/hourly-group-subtypes';
  return '';
};

// Add new item
const handleAddItem = async () => {
  if (!newItemName.value.trim()) return;

  isSubmitting.value = true;
  try {
    const payload = activeTab.value === 'subjects'
      ? { subjectName: newItemName.value.trim() }
      : { subtypeName: newItemName.value.trim() };

    await api.post(getEndpoint(), payload);
    
    successMessage.value = `เพิ่ม${getTabLabel()}สำเร็จ!`;
    newItemName.value = '';
    await loadData();
    
    setTimeout(() => successMessage.value = '', 3000);
  } catch (error) {
    console.error('Error adding item:', error);
    errorMessage.value = error.response?.data?.message || `ไม่สามารถเพิ่ม${getTabLabel()}ได้`;
    setTimeout(() => errorMessage.value = '', 5000);
  } finally {
    isSubmitting.value = false;
  }
};

// Start editing
const startEdit = (item) => {
  editingId.value = item.id;
  editValue.value = item.subjectName || item.subtypeName;
};

// Cancel edit
const cancelEdit = () => {
  editingId.value = null;
  editValue.value = '';
};

// Save edit
const handleSaveEdit = async (id) => {
  if (!editValue.value.trim()) return;

  try {
    const payload = activeTab.value === 'subjects'
      ? { subjectName: editValue.value.trim() }
      : { subtypeName: editValue.value.trim() };

    await api.put(`${getEndpoint()}/${id}`, payload);
    
    successMessage.value = `แก้ไข${getTabLabel()}สำเร็จ!`;
    cancelEdit();
    await loadData();
    
    setTimeout(() => successMessage.value = '', 3000);
  } catch (error) {
    console.error('Error updating item:', error);
    errorMessage.value = error.response?.data?.message || `ไม่สามารถแก้ไข${getTabLabel()}ได้`;
    setTimeout(() => errorMessage.value = '', 5000);
  }
};

// Toggle active status
const handleToggleActive = async (item) => {
  try {
    const newStatus = !item.isActive;
    await api.patch(`${getEndpoint()}/${item.id}/toggle-active`, { isActive: newStatus });
    
    successMessage.value = `${newStatus ? 'เปิด' : 'ปิด'}ใช้งาน${getTabLabel()}สำเร็จ!`;
    await loadData();
    
    setTimeout(() => successMessage.value = '', 3000);
  } catch (error) {
    console.error('Error toggling active:', error);
    errorMessage.value = `ไม่สามารถเปลี่ยนสถานะ${getTabLabel()}ได้`;
    setTimeout(() => errorMessage.value = '', 5000);
  }
};

// Delete item
const handleDelete = async (id) => {
  if (!confirm(`คุณแน่ใจหรือไม่ที่จะลบ${getTabLabel()}นี้?`)) return;

  try {
    await api.delete(`${getEndpoint()}/${id}`);
    
    successMessage.value = `ลบ${getTabLabel()}สำเร็จ!`;
    await loadData();
    
    setTimeout(() => successMessage.value = '', 3000);
  } catch (error) {
    console.error('Error deleting item:', error);
    errorMessage.value = error.response?.data?.message || `ไม่สามารถลบ${getTabLabel()}ได้`;
    setTimeout(() => errorMessage.value = '', 5000);
  }
};

// Watch tab changes
const handleTabChange = () => {
  cancelEdit();
  newItemName.value = '';
  loadData();
};

// Load initial data
onMounted(() => {
  loadData();
});

// Watch tab changes
watch(activeTab, () => {
  handleTabChange();
});
</script>

<style scoped>
@media (max-width: 640px) {
  .container {
    padding-top: 1rem;
  }
}
</style>