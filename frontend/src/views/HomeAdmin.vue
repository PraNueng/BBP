<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-blue-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-yellow-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
      <div class="absolute w-96 h-96 bg-amber-500/20 rounded-full blur-3xl top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 animate-pulse delay-2000"></div>
    </div>

    <NavBar />

    <div class="container mx-auto px-8 lg:px-8 py-16 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="text-center mb-16">
        <h1 class="text-6xl font-bold bg-gradient-to-r from-blue-400 to-yellow-400 bg-clip-text text-transparent">
          ระบบจัดการ
        </h1>
        <p class="text-gray-300 text-lg">เลือกส่วนที่ต้องการจัดการ</p>
      </div>

      <!-- Cards Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
        <!-- Student Card -->
        <div
          @click="goToPage('/student-management-total')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from--500 to-cyan-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-cyan-500/50">
            <div class="flex flex-col items-center space-y-6">
              <!-- Icon Container -->
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-2xl blur-md group-hover:blur-lg transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-blue-500 to-cyan-500 w-24 h-24 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                  </svg>
                </div>
              </div>

              <!-- Title -->
              <div class="text-center">
                <h2 class="text-3xl font-bold text-white mb-2 group-hover:text--400 transition-colors duration-300">
                  นักเรียน
                </h2>
                <p class="text-gray-400 text-sm">จัดการข้อมูลนักเรียน</p>
              </div>

              <!-- Arrow Icon -->
              <div class="mt-4 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Teacher Card -->
        <div
          @click="goToPage('/tutor-management-total')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-indigo-500 to-blue-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-blue-500/50">
            <div class="flex flex-col items-center space-y-6">
              <!-- Icon Container -->
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-br from-indigo-500 to-blue-500 rounded-2xl blur-md group-hover:blur-lg transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-indigo-500 to-blue-500 w-24 h-24 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                  </svg>
                </div>
              </div>

              <!-- Title -->
              <div class="text-center">
                <h2 class="text-3xl font-bold text-white mb-2 group-hover:text-blue-400 transition-colors duration-300">
                  ครู
                </h2>
                <p class="text-gray-400 text-sm">จัดการข้อมูลครู</p>
              </div>

              <!-- Arrow Icon -->
              <div class="mt-4 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Class Card -->
        <div
          @click="goToPage('/class-management-total')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-yellow-500 to-amber-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-amber-500/50">
            <div class="flex flex-col items-center space-y-6">
              <!-- Icon Container -->
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-br from-yellow-500 to-amber-500 rounded-2xl blur-md group-hover:blur-lg transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-yellow-500 to-amber-500 w-24 h-24 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
                  </svg>
                </div>
              </div>

              <!-- Title -->
              <div class="text-center">
                <h2 class="text-3xl font-bold text-white mb-2 group-hover:text-amber-400 transition-colors duration-300">
                  คลาส
                </h2>
                <p class="text-gray-400 text-sm">จัดการข้อมูลคลาส</p>
              </div>

              <!-- Arrow Icon -->
              <div class="mt-4 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goToPage = (path) => {
  router.push(path);
};
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.delay-1000 {
  animation-delay: 1s;
}

.delay-2000 {
  animation-delay: 2s;
}

/* Responsive */
@media (max-width: 640px) {
  .container {
    padding-top: 2rem;
  }
  h2 {
    font-size: 1.75rem;
  }
}
</style>