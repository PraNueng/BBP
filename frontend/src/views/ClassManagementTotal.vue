<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-slate-900 via-amber-900 to-yellow-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-yellow-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-amber-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
    </div>

    <NavBar />
    <BackButton to="/home-admin" class="pt-8"/>

    <div class="container mx-auto px-8 lg:px-8 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-black text-white mb-2">
          จัดการ<span class="bg-gradient-to-r from-yellow-400 to-amber-400 bg-clip-text text-transparent">คลาส</span>
        </h1>
        <p class="text-gray-300">เลือกเมนูที่ต้องการ</p>
      </div>

      <!-- Main Actions -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto mb-12">
        <!-- View Classes -->
        <div
          @click="goToPage('/add-classes')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-amber-500 to-orange-500 rounded-2xl blur-lg opacity-50 group-hover:opacity-75 transition-all duration-300"></div>
            <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-amber-500/50">
              <div class="flex items-center space-x-4">
                <div class="rounded-xl bg-gradient-to-br from-amber-500 to-orange-500 w-16 h-16 flex items-center justify-center">
                  <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
                  </svg>
              </div>

              <!-- Title -->
              <div class="flex-1">
                <h2 class="text-xl font-bold text-white group-hover:text-amber-400 transition-colors text-center">
                  สร้าง/แก้ไขข้อมูลคลาส
                </h2>
              </div>
              <svg class="w-6 h-6 text-amber-400 opacity-0 group-hover:opacity-100 transition-opacity" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Add Class -->
        <div
          @click="goToPage('/class-management')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-yellow-500 to-amber-500 rounded-2xl blur-lg opacity-50 group-hover:opacity-75 transition-all duration-300"></div>
            <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-yellow-500/50">
              <div class="flex items-center space-x-4">
                <div class="rounded-xl bg-gradient-to-br from-yellow-500 to-amber-500 w-16 h-16 flex items-center justify-center">
                  <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                  </svg> 
                </div>

              <!-- Title -->
              <div class="flex-1">
                <h2 class="text-xl font-bold text-white group-hover:text-yellow-400 transition-colors text-center">
                  เพิ่มวิชา/ประเภทคลาสใหม่ในระบบ
                </h2>
              </div>
              <svg class="w-6 h-6 text-yellow-400 opacity-0 group-hover:opacity-100 transition-opacity" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </div>
        </div>

        <div
          @click="goToPage('/view-all-classes')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-500 rounded-2xl blur-lg opacity-50 group-hover:opacity-75 transition-all duration-300"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-blue-500/50">
            <div class="flex items-center space-x-4">
              <div class="rounded-xl bg-gradient-to-br from-blue-500 to-indigo-500 w-16 h-16 flex items-center justify-center">
                <Eye class="w-8 h-8 text-white" />
              </div>
              <div class="flex-1">
                <h2 class="text-xl font-bold text-white group-hover:text-blue-400 transition-colors text-center">
                  คลาสทั้งหมด
                </h2>
              </div>
              <svg class="w-6 h-6 text-blue-400 opacity-0 group-hover:opacity-100 transition-opacity" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
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
import BackButton from '@/components/BackButton.vue';
import { Eye } from 'lucide-vue-next';

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