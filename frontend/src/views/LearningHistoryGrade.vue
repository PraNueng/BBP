<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-blue-50 to-blue-100 overflow-y-auto">
    <NavBar />
    <BackButton to="/home-admin" class="order-1 md:order-1 pl-6 pt-8" />
    <div class="container mx-auto px-8 lg:px-8 py-16 max-w-7xl">
      <h1 class="text-3xl sm:text-4xl font-bold text-center text-purple-700 mb-12">
        เลือกระดับชั้นของน้อง
      </h1>

      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="errorMessage" class="text-center py-12">
        <div class="text-red-600 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <p class="text-gray-700 text-lg">{{ errorMessage }}</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="grades.length === 0" class="text-center py-12">
        <div class="text-gray-400 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
        </div>
        <p class="text-gray-600 text-lg">ยังไม่มีนักเรียนในระบบ</p>
        <p class="text-gray-500 text-sm mt-2">กรุณาเพิ่มนักเรียนก่อนเพื่อเริ่มใช้งาน</p>
      </div>

      <!-- กล่องระดับชั้น -->
      <div v-else class="flex flex-wrap justify-center gap-6">
        <div
          v-for="grade in grades"
          :key="grade.id"
          @click="goToGradePage(grade)"
          class="group cursor-pointer bg-white text-purple-700 rounded-2xl shadow-lg w-40 h-40 flex flex-col items-center justify-center 
                hover:bg-gradient-to-br hover:from-purple-500 hover:to-purple-400 hover:text-white transition-all transform hover:scale-105"
        >
          <div class="text-4xl font-bold">{{ grade.gradeName }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import { useRouter } from 'vue-router';
import api from '@/api.js';

const router = useRouter();
const grades = ref([]);
const isLoading = ref(false);
const errorMessage = ref('');

// Grade ID to route path mapping
const gradeRouteMap = {
  1: '/learning-history-grade-m1',
  2: '/learning-history-grade-m2',
  3: '/learning-history-grade-m3',
  4: '/learning-history-grade-m4',
  5: '/learning-history-grade-m5',
  6: '/learning-history-grade-m6',
  7: '/learning-history-grade-others'
};

/**
 * โหลด Grade ที่มีนักเรียน active อยู่
 */
const loadGradesWithStudents = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  
  try {
    const response = await api.get('/grades/with-students');
    grades.value = response.data || [];
  } catch (error) {
    console.error('Error loading grades:', error);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลระดับชั้นได้';
  } finally {
    isLoading.value = false;
  }
};

/**
 * ไปหน้ารายละเอียดของแต่ละ Grade
 */
const goToGradePage = (grade) => {
  const route = gradeRouteMap[grade.id];
  if (route) {
    router.push(route);
  } else {
    console.warn('Route not found for grade:', grade);
  }
};

onMounted(() => {
  loadGradesWithStudents();
});
</script>

<style scoped>
img {
  object-fit: contain;
}

/* Responsive */
@media (max-width: 640px) {
  .container {
    padding-top: 2rem;
  }
  h1 {
    font-size: 1.75rem;
  }
}
</style>