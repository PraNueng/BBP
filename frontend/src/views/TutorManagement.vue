<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-900 to-blue-900">
    <NavBar />
    <BackButton to="/tutor-management-total" class="pt-8" />

    <div class="container mx-auto px-4 lg:px-8 py-8 max-w-7xl">
      <!-- Header -->
      <div class="bg-gradient-to-r from-indigo-800 to-blue-700 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          จัดการข้อมูลครู
        </h1>
        <p class="text-purple-50 text-center mt-3 text-lg">
          เพิ่มครูผู้สอนใหม่เข้าระบบและจัดการข้อมูลครู
        </p>
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- messages moved to fixed bottom-right toasts (see end of file) -->

        <!-- Search & Add Button -->
        <div class="mb-8 flex flex-col md:flex-row gap-4">
          <div class="flex-1">
            <SearchBar
              v-model="searchQuery"
              placeholder="ค้นหาชื่อของครูผู้สอน"
              @search="handleSearch"
            />
          </div>
          <button
            @click="openAddModal"
            class="px-6 py-3 bg-gradient-to-r from-indigo-800 to-blue-700 text-white font-bold rounded-lg hover:from-indigo-600 hover:to-blue-600 transition-all shadow-lg flex items-center justify-center gap-2"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            เพิ่มผู้สอนใหม่
          </button>
        </div>

        <!-- Filter Buttons -->
        <div class="mb-6 flex gap-2 flex-wrap justify-between items-center">
          <button
            @click="filterStatus = 'all'"
            :class="[
              'px-4 py-2 rounded-lg font-semibold transition-all',
              filterStatus === 'all'
                ? 'bg-gray-300 text-black-200 hover:bg-gray-300'
                : 'bg-gray-300 text-black-200 hover:bg-gray-300'
            ]"
          >
            ทั้งหมด ({{ tutors.length }})
          </button>

          <button
            @click="openManageAllClassesModal"
            class="px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-500 text-white rounded-lg hover:from-blue-600 hover:to-indigo-600 transition font-semibold flex items-center gap-2 shadow-lg"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            เพิ่มครูเข้าคลาส
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>

        <!-- Tutors Grid -->
        <div v-else-if="filteredTutors.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="tutor in filteredTutors"
            :key="tutor.id"
            class="bg-gradient-to-br from-white to-purple-50 border-2 border-purple-200 rounded-xl p-6 hover:shadow-xl transition-all flex flex-col h-full"
          >
            <!-- Tutor Header -->
            <div class="flex items-start justify-between mb-4">
              <div class="flex items-center gap-3">
                <div class="w-14 h-14 rounded-full bg-gradient-to-r from-indigo-700 to-blue-600 flex items-center justify-center text-white">
                  <!-- User Icon SVG -->
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <div>
                  <h3 class="text-xl font-bold text-gray-800">{{ tutor.nickname || tutor.username }}</h3>
                  <p class="text-sm text-gray-500">@{{ tutor.username }}</p>
                </div>
              </div>
              <div>
                <EditButton @edit="openEditModal(tutor)" class="scale-90 mt-2" />
              </div>
            </div>

            <!-- Statistics -->
            <div class="mb-4 space-y-2">
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">คลาสทั้งหมด:</span>
                <span class="font-bold text-purple-600">
                  {{ getTotalClasses(tutor) }} คลาส
                </span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายเดือน:</span>
                <span class="font-bold">{{ tutor.totalMonthlyClasses }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายชั่วโมงแบบกลุ่ม:</span>
                <span class="font-bold">{{ tutor.totalHourlyGroupClasses }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายชั่วโมงแบบเดี่ยว:</span>
                <span class="font-bold">{{ tutor.totalHourlyIndividualClasses }}</span>
              </div>
            </div>

            <!-- Subjects -->
            <div v-if="tutor.subjects && tutor.subjects.length > 0" class="mb-4">
              <p class="text-xs text-gray-500 mb-2">วิชาที่สอน:</p>
              <div class="flex flex-wrap gap-1">
                <span
                  v-for="subject in tutor.subjects"
                  :key="subject.id"
                  class="px-2 py-1 bg-purple-100 text-purple-700 text-xs rounded-full font-medium"
                >
                  {{ subject.name }} ({{ subject.classCount }})
                </span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="mt-auto flex flex-col gap-2">
              <!-- ปุ่มดูคลาสทั้งหมด -->
              <button
                @click="openTutorClasses(tutor)"
                class="flex-1 py-2 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-lg hover:from-indigo-600 hover:to-purple-600 transition font-semibold flex items-center justify-center gap-2 text-center"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                ดูคลาสทั้งหมด
              </button>

              <!-- ปุ่มดูประวัติการสอน -->
              <button
                @click="goToTeachingHistory(tutor)"
                class="flex-1 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 transition font-semibold flex items-center justify-center gap-2 text-center"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                ดูประวัติการสอน
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <p class="mt-4 text-lg text-gray-600">ไม่พบข้อมูลผู้สอน</p>
        </div>
      </div>
    </div>

    <!-- Add/Edit Modal -->
    <div
      v-if="showModal"
      class="fixed inset-0 backdrop-blur-sm bg-black/20 flex items-center justify-center p-4 z-50"
      @click.self="closeModal"
    >
      <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full p-8">
        <h2 class="text-2xl font-bold text-gray-800 mb-6">
          {{ isEditMode ? 'แก้ไขข้อมูลผู้สอน' : 'เพิ่มผู้สอนใหม่' }}
        </h2>

        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">ชื่อผู้ใช้ (Username)</label>
            <input
              v-model="formData.username"
              type="text"
              required
              :disabled="isEditMode"
              :class="[
                'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-purple-500',
                isEditMode ? 'bg-gray-100 cursor-not-allowed' : 'border-gray-300'
              ]"
              placeholder="username"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">ชื่อเล่น (Nickname)</label>
            <input
              v-model="formData.nickname"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
              placeholder="ชื่อเล่น"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              {{ isEditMode ? 'รหัสผ่านใหม่ (ไม่ระบุหากไม่ต้องการเปลี่ยน)' : 'รหัสผ่าน' }}
            </label>
            <input
              v-model="formData.password"
              type="password"
              :required="!isEditMode"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
              placeholder="รหัสผ่าน"
            />
          </div>

          <div v-if="!isEditMode">
            <label for="confirmPassword" class="block text-sm font-semibold text-gray-700 mb-2">ยืนยันรหัสผ่าน</label>
            <input
              id="confirmPassword"
              v-model="formData.confirmPassword"
              type="password"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
              placeholder="ยืนยันรหัสผ่าน"
              aria-label="ยืนยันรหัสผ่าน"
            />
          </div>

          <div class="flex gap-3 mt-6">
            <button
              type="button"
              @click="closeModal"
              class="flex-1 px-4 py-3 bg-gray-300 text-gray-700 font-bold rounded-lg hover:bg-gray-400 transition"
            >
              ยกเลิก
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white font-bold rounded-lg hover:from-purple-600 hover:to-pink-600 transition disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed"
            >
              {{ isSubmitting ? 'กำลังบันทึก...' : (isEditMode ? 'บันทึก' : 'ตกลง') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Manage Classes Modal -->
    <ClassManagementModal
      :show="showManageClassesModal"
      :tutor="null"
      @close="closeManageClassesModal"
      @updated="handleClassesUpdated"
    />

    <TutorClassesView
      :show="showTutorClassesModal"
      :tutorId="detailTutorId"
      @close="closeTutorClasses"
      @edit="handleTutorModalEdit"
    />

    <!-- Fixed corner notifications (bottom-right) matching AddClass.vue -->
    <div :class="['fixed right-6 bottom-6 flex flex-col items-end gap-3', showModal ? 'z-100' : 'z-90']" aria-live="polite">
      <transition-group name="slide-fade" tag="div">
        <div v-if="successMessage" key="success" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-green-500 to-emerald-600 text-white shadow-lg flex items-center gap-4">
          <CheckCircle class="w-7 h-7" />
          <div class="text-base font-semibold">{{ successMessage }}</div>
        </div>
        <div v-if="errorMessage" key="error" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-red-500 to-pink-600 text-white shadow-lg flex items-center gap-4">
          <AlertCircle class="w-7 h-7" />
          <div class="text-base font-semibold">{{ errorMessage }}</div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import EditButton from '@/components/EditButton.vue';
import SearchBar from '@/components/SearchBar.vue';
import api from '@/api.js';
import ClassManagementModal from '@/components/ClassManagementModal.vue';
import TutorClassesView from '@/modals/TutorClassesView.vue';
import { useRouter } from 'vue-router';
import { CheckCircle, AlertCircle } from 'lucide-vue-next';

const tutors = ref([]);
const searchQuery = ref('');
const filterStatus = ref('all');
const isLoading = ref(false);
const isSubmitting = ref(false);
const successMessage = ref('');
const errorMessage = ref('');
const router = useRouter();

// Modal state
const showModal = ref(false);
const isEditMode = ref(false);
const editingTutor = ref(null);
const formData = ref({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
});

const goToTeachingHistory = (tutor) => {
  router.push(`/list-hour-form-tutor?tutorId=${tutor.id}&tutorName=${encodeURIComponent(tutor.nickname || tutor.username)}`);
};

// Manage Classes Modal state
const showManageClassesModal = ref(false);
const selectedTutor = ref(null);

// Tutor classes modal state
const showTutorClassesModal = ref(false);
const detailTutorId = ref(null);

// Computed
const filteredTutors = computed(() => {
  let filtered = tutors.value;

  if (filterStatus.value === 'active') {
    filtered = filtered.filter(t => t.isActive);
  } else if (filterStatus.value === 'inactive') {
    filtered = filtered.filter(t => !t.isActive);
  }

  return filtered;
});

const activeTutorsCount = computed(() => tutors.value.filter(t => t.isActive).length);
const inactiveTutorsCount = computed(() => tutors.value.filter(t => !t.isActive).length);

// Methods
const loadTutors = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/tutors');
    tutors.value = response.data;
  } catch (error) {
    console.error('Error loading tutors:', error);
    showError('ไม่สามารถโหลดข้อมูลผู้สอนได้');
  } finally {
    isLoading.value = false;
  }
};

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    await loadTutors();
    return;
  }

  isLoading.value = true;
  try {
    const response = await api.get('/tutors', {
      params: { search: searchQuery.value }
    });
    tutors.value = response.data;
  } catch (error) {
    console.error('Error searching tutors:', error);
    showError('ไม่สามารถค้นหาได้');
  } finally {
    isLoading.value = false;
  }
};

const openAddModal = () => {
  isEditMode.value = false;
  editingTutor.value = null;
  formData.value = {
    username: '',
    nickname: '',
    password: '',
    confirmPassword: ''
  };
  showModal.value = true;
};

const openEditModal = (tutor) => {
  isEditMode.value = true;
  editingTutor.value = tutor;
  formData.value = {
    username: tutor.username,
    nickname: tutor.nickname || '',
    password: '',
    confirmPassword: ''
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  isEditMode.value = false;
  editingTutor.value = null;
  formData.value = {
    username: '',
    nickname: '',
    password: '',
    confirmPassword: ''
  };
};

const handleSubmit = async () => {
  // Before submitting, validate confirm password when creating a new tutor
  if (!isEditMode.value) {
    // adding new tutor: password and confirmPassword are required and must match
    if (!formData.value.password || !formData.value.confirmPassword) {
      showError('กรุณากรอกทั้งรหัสผ่านและยืนยันรหัสผ่าน');
      return;
    }
    if (formData.value.password !== formData.value.confirmPassword) {
      showError('รหัสผ่านและการยืนยันรหัสผ่านไม่ตรงกัน');
      return;
    }
  }

  isSubmitting.value = true;
  try {
    if (isEditMode.value) {
      const payload = {
        nickname: formData.value.nickname || null
      };
      if (formData.value.password) {
        payload.password = formData.value.password;
      }
      await api.put(`/tutors/${editingTutor.value.id}`, payload);
      showSuccess('แก้ไขข้อมูลผู้สอนสำเร็จ!');
    } else {
      await api.post('/tutors', formData.value);
      showSuccess('เพิ่มผู้สอนสำเร็จ!');
    }
    closeModal();
    await loadTutors();
  } catch (error) {
    console.error('Error submitting form:', error);
    showError(error.response?.data?.message || 'เกิดข้อผิดพลาด');
  } finally {
    isSubmitting.value = false;
  }
};

const openManageAllClassesModal = () => {
  showManageClassesModal.value = true;
};

const openManageClassesModal = (tutor) => {
  selectedTutor.value = tutor;
  showManageClassesModal.value = true;
};

const closeManageClassesModal = () => {
  showManageClassesModal.value = false;
  selectedTutor.value = null;
};

const handleClassesUpdated = async () => {
  await loadTutors();
  showSuccess('เพิ่มครูสำเร็จ!');
};

const openTutorClasses = (tutor) => {
  detailTutorId.value = tutor.id;
  showTutorClassesModal.value = true;
};

const closeTutorClasses = () => {
  showTutorClassesModal.value = false;
  detailTutorId.value = null;
};

const handleTutorModalEdit = (tutorWithClasses) => {
  // Reuse existing edit modal for tutors
  openEditModal(tutorWithClasses);
  closeTutorClasses();
};

const getTotalClasses = (tutor) => {
  return tutor.totalMonthlyClasses + tutor.totalHourlyGroupClasses + tutor.totalHourlyIndividualClasses;
};

const getInitials = (name) => {
  if (!name) return '?';
  const words = name.split(' ');
  if (words.length >= 2) {
    return (words[0][0] + words[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
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

onMounted(() => {
  loadTutors();
});
</script>

<style scoped>
@media (max-width: 640px) {
  .container {
    padding-top: 1rem;
  }
}
</style>