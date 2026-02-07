<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-50 to-pink-100">
    <NavBar />
    <BackButton :to="`/tutor-management`" class="pt-8" />

    <div class="container mx-auto px-4 lg:px-8 py-8 max-w-7xl">
      <!-- Header -->
      <div class="bg-gradient-to-r from-purple-600 to-pink-600 rounded-t-3xl shadow-2xl p-8">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-5xl font-extrabold text-white drop-shadow-lg flex items-center gap-3">
              <div class="w-14 h-14 bg-white/20 rounded-full flex items-center justify-center">
                {{ getTutorInitial() }}
              </div>
              {{ tutor?.nickname || tutor?.username }}
            </h1>
            <p class="text-purple-50 mt-3 text-lg">
              คลาสทั้งหมดที่สอน: {{ getTotalClasses() }} คลาส
            </p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
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

        <!-- Tabs -->
        <div class="flex gap-4 mb-8 overflow-x-auto pb-2">
          <button
            @click="activeTab = 'monthly'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'monthly'
                ? 'bg-gradient-to-r from-green-500 to-green-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            คลาสรายเดือน ({{ monthlyClasses.length }})
          </button>
          <button
            @click="activeTab = 'hourly-group'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-group'
                ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            กลุ่มรายชั่วโมง ({{ hourlyGroupClasses.length }})
          </button>
          <button
            @click="activeTab = 'hourly-individual'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-individual'
                ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            เดี่ยวรายชั่วโมง ({{ hourlyIndividualClasses.length }})
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>

        <!-- Classes List -->
        <div v-else>
          <!-- Monthly Classes -->
          <div v-show="activeTab === 'monthly'">
            <div v-if="monthlyClasses.length === 0" class="text-center py-12">
              <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
              </svg>
              <p class="mt-4 text-lg text-gray-600">ยังไม่มีคลาสรายเดือน</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <ClassCard
                v-for="cls in monthlyClasses"
                :key="cls.id"
                :class-data="cls"
                class-type="monthly"
                @manage-tutors="openManageTutorsModal(cls, 'monthly')"
              />
            </div>
          </div>

          <!-- Hourly Group Classes -->
          <div v-show="activeTab === 'hourly-group'">
            <div v-if="hourlyGroupClasses.length === 0" class="text-center py-12">
              <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              <p class="mt-4 text-lg text-gray-600">ยังไม่มีกลุ่มรายชั่วโมง</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <ClassCard
                v-for="cls in hourlyGroupClasses"
                :key="cls.id"
                :class-data="cls"
                class-type="hourly-group"
                @manage-tutors="openManageTutorsModal(cls, 'hourly-group')"
              />
            </div>
          </div>

          <!-- Hourly Individual Classes -->
          <div v-show="activeTab === 'hourly-individual'">
            <div v-if="hourlyIndividualClasses.length === 0" class="text-center py-12">
              <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <p class="mt-4 text-lg text-gray-600">ยังไม่มีคลาสตัวต่อตัว</p>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <ClassCard
                v-for="cls in hourlyIndividualClasses"
                :key="cls.id"
                :class-data="cls"
                class-type="hourly-individual"
                @manage-tutors="openManageTutorsModal(cls, 'hourly-individual')"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Manage Tutors Modal -->
    <ManageTutorsModal
      v-if="showManageTutorsModal"
      :show="showManageTutorsModal"
      :class-data="selectedClass"
      :class-type="selectedClassType"
      @close="closeManageTutorsModal"
      @success="handleManageTutorsSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import ManageTutorsModal from '@/modals/ManageTutorsModal.vue';
import ClassCard from '@/components/ClassCard.vue';
import api from '@/api.js';

const route = useRoute();
const tutorId = ref(parseInt(route.params.id));

const tutor = ref(null);
const activeTab = ref('monthly');
const isLoading = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

const monthlyClasses = ref([]);
const hourlyGroupClasses = ref([]);
const hourlyIndividualClasses = ref([]);

const showManageTutorsModal = ref(false);
const selectedClass = ref(null);
const selectedClassType = ref('');

const getTotalClasses = () => {
  return monthlyClasses.value.length + hourlyGroupClasses.value.length + hourlyIndividualClasses.value.length;
};

const getTutorInitial = () => {
  const name = tutor.value?.nickname || tutor.value?.username;
  if (!name) return '?';
  const words = name.split(' ');
  if (words.length >= 2) {
    return (words[0][0] + words[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
};

const loadTutorData = async () => {
  try {
    const response = await api.get(`/tutors/${tutorId.value}`);
    tutor.value = response.data;
  } catch (error) {
    console.error('Error loading tutor:', error);
    showError('ไม่สามารถโหลดข้อมูลครูได้');
  }
};

const loadClasses = async () => {
  isLoading.value = true;
  try {
    const [monthlyRes, hourlyGroupRes, hourlyIndividualRes] = await Promise.all([
      api.get('/monthly-classes', { params: { tutorId: tutorId.value, active: true } }),
      api.get('/hourly-group-classes', { params: { tutorId: tutorId.value, active: true } }),
      api.get('/hourly-individual-classes', { params: { tutorId: tutorId.value, active: true } })
    ]);

    monthlyClasses.value = monthlyRes.data;
    hourlyGroupClasses.value = hourlyGroupRes.data;
    hourlyIndividualClasses.value = hourlyIndividualRes.data;

    // ดึงข้อมูลครูสำหรับแต่ละคลาสเดี่ยว
    await Promise.all(
      hourlyIndividualClasses.value.map(async (cls) => {
        try {
          const tutorsResponse = await api.get(`/hourly-individual-classes/${cls.id}/tutors`);
          cls.tutors = tutorsResponse.data;
          cls.totalTutors = tutorsResponse.data.length;
        } catch (error) {
          console.warn(`Cannot fetch tutors for class ${cls.id}:`, error);
          cls.tutors = [];
          cls.totalTutors = 0;
        }
      })
    );
  } catch (error) {
    console.error('Error loading classes:', error);
    showError('ไม่สามารถโหลดคลาสได้');
  } finally {
    isLoading.value = false;
  }
};

const openManageTutorsModal = (cls, type) => {
  selectedClass.value = cls;
  selectedClassType.value = type;
  showManageTutorsModal.value = true;
};

const closeManageTutorsModal = () => {
  showManageTutorsModal.value = false;
  selectedClass.value = null;
  selectedClassType.value = '';
};

const handleManageTutorsSuccess = async (message) => {
  showSuccess(message);
  await loadTutorData();
  await loadClasses();
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

onMounted(async () => {
  await loadTutorData();
  await loadClasses();
});
</script>