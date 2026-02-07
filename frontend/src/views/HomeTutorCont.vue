<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-red-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-yellow-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
      <div class="absolute w-96 h-96 bg-green-500/20 rounded-full blur-3xl top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 animate-pulse delay-2000"></div>
    </div>

    <NavBar />
    <BackButton :to="'/home-tutor'" class="pt-6"/>

    <div class="container mx-auto px-8 lg:px-8 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="text-center mb-10 mt-12">
        <h1 class="text-3xl md:text-6xl font-bold bg-gradient-to-r from-blue-400 to-yellow-400 bg-clip-text text-transparent">
          แบบฟอร์มรายเดือน
        </h1>
        <!-- <p class="text-gray-300 text-lg">เลือกแบบฟอร์มที่ต้องการ</p> -->
      </div>

      <!-- Cards Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-1 gap-8 max-w-100 mx-auto auto-rows-fr min-h-[200px]">
        <!-- Math Card -->
        <div
          @click="goToForm('/math-forms')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-red-500 to-pink-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-red-500/50 flex items-center justify-center">
            <div class="flex flex-col items-center space-y-6">
              
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-br from-red-500 to-pink-500 rounded-2xl blur-md group-hover:blur-lg transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-red-500 to-pink-500 w-24 h-24 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <img src="../assets/math.svg" alt="Math Icon" class="w-12 h-12 brightness-0 invert" />
                </div>
              </div>

              <div class="text-center">
                <h2 class="text-3xl font-bold text-white mb-2 group-hover:text-red-400 transition-colors duration-300">
                  คณิตศาสตร์
                </h2>
              </div>

              <div class="mt-4 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Science Card -->
        <div
          @click="goToForm('/science-forms')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-green-500 to-emerald-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-green-500/50 flex items-center justify-center">
            <div class="flex flex-col items-center space-y-6">
              
              <div class="relative">
                <div class="absolute inset-0 bg-gradient-to-br from-green-500 to-emerald-500 rounded-2xl blur-md group-hover:blur-lg transition-all duration-500"></div>
                <div class="relative rounded-2xl bg-gradient-to-br from-green-500 to-emerald-500 w-24 h-24 flex items-center justify-center transform group-hover:rotate-6 transition-all duration-500">
                  <img src="../assets/science.svg" alt="Science Icon" class="w-12 h-12 brightness-0 invert" />
                </div>
              </div>

              <div class="text-center">
                <h2 class="text-3xl font-bold text-white mb-2 group-hover:text-green-400 transition-colors duration-300">
                  วิทยาศาสตร์
                </h2>
              </div>

              <div class="mt-4 opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
import BackButton from '@/components/BackButton.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const goToForm = (path) => {
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
