<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-slate-900 via-indigo-900 to-blue-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-blue-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
    </div>

    <NavBar />
    <BackButton to="/home-admin" class="pt-8"/>

    <div class="container mx-auto px-8 lg:px-8 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-black text-white mb-2">
          จัดการ<span class="bg-gradient-to-r from-blue-400 to-indigo-400 bg-clip-text text-transparent">ครู</span>
        </h1>
        <p class="text-gray-300">เลือกเมนูที่ต้องการ</p>
      </div>

      <!-- Main Action -->
      <div class="max-w-2xl mx-auto">
        <div
          @click="goToPage('/tutor-management')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-indigo-500 to-blue-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-10 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-indigo-500/50">
            <div class="flex flex-col md:flex-row items-center md:space-x-8 space-y-6 md:space-y-0">
              <!-- Icon Container -->
              <div class="relative flex-shrink-0">
                <div class="absolute inset-0 bg-gradient-to-br from-indigo-500 to-blue-500 rounded-2xl blur-lg group-hover:blur-xl transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-indigo-500 to-blue-500 w-28 h-28 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <svg class="w-14 h-14 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                  </svg>
                </div>
              </div>

              <!-- Content -->
              <div class="flex-1 text-center md:text-left">
                <h2 class="text-3xl font-bold text-white mb-3 group-hover:text-blue-400 transition-colors duration-300">
                  จัดการข้อมูลครู
                </h2>
                <p class="text-gray-400 text-lg">เพิ่มครูผู้สอนใหม่เข้าระบบและจัดการข้อมูลครู</p>
              </div>

              <!-- Arrow Icon -->
              <div class="flex-shrink-0 opacity-0 group-hover:opacity-100 transform translate-x-0 group-hover:translate-x-2 transition-all duration-300">
                <svg class="w-10 h-10 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
import BackButton from '@/components/BackButton.vue';
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

/* Responsive */
@media (max-width: 640px) {
  .container {
    padding-top: 2rem;
  }
}
</style>