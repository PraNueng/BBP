<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-blue-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
    </div>

    <NavBar />
    <BackButton to="/home-admin" class="pt-8"/>

    <div class="container mx-auto px-8 lg:px-8 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-black text-white mb-2">
          จัดการ<span class="bg-gradient-to-r from-cyan-400 to-blue-400 bg-clip-text text-transparent">นักเรียน</span>
        </h1>
        <p class="text-gray-300">เลือกเมนูที่ต้องการ</p>
      </div>

      <!-- Main Actions -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-5xl mx-auto mb-12">
        <!-- Add Student -->
        <div
          @click="goToPage('/students')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-2xl blur-lg opacity-50 group-hover:opacity-75 transition-all duration-300"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-cyan-500/50">
            <div class="flex items-center space-x-4">
              <div class="rounded-xl bg-gradient-to-br from-cyan-500 to-blue-500 w-16 h-16 flex items-center justify-center">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                </svg>
              </div>
              <div class="flex-1">
                <h2 class="text-2xl font-bold text-white group-hover:text-cyan-400 transition-colors text-center">
                  เพิ่มนักเรียนใหม่ในระบบ
                </h2>
              </div>
              <svg class="w-6 h-6 text-cyan-400 opacity-0 group-hover:opacity-100 transition-opacity" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </div>
        </div>

        <!-- Student Management Like Tutor -->
        <div
          @click="goToPage('/student-management-like-tutor')"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-500 rounded-2xl blur-lg opacity-50 group-hover:opacity-75 transition-all duration-300"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-2xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-indigo-500/50">
            <div class="flex items-center space-x-4">
              <div class="rounded-xl bg-gradient-to-br from-blue-500 to-indigo-500 w-16 h-16 flex items-center justify-center">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
              </div>
              <div class="flex-1">
                <h2 class="text-2xl font-bold text-white group-hover:text-indigo-400 transition-colors text-center">
                  ข้อมูลนักเรียนในระบบ
                </h2>
              </div>
              <svg class="w-6 h-6 text-indigo-400 opacity-0 group-hover:opacity-100 transition-opacity" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Grade Levels -->
      <!-- <div class="max-w-6xl mx-auto">
        <h3 class="text-2xl font-bold text-white mb-6 text-center">เลือกตามระดับชั้น</h3>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="md:col-span-3 grid grid-cols-3 gap-4">
            <div
              @click="goToPage('/learning-history-grade-m1')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-cyan-400 to-blue-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-cyan-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-cyan-400 transition-colors">ม.1</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 1</div>
                </div>
              </div>
            </div>

            <div
              @click="goToPage('/learning-history-grade-m2')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-blue-400 to-indigo-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-blue-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-blue-400 transition-colors">ม.2</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 2</div>
                </div>
              </div>
            </div>

            <div
              @click="goToPage('/learning-history-grade-m3')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-indigo-400 to-blue-500 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-indigo-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-indigo-400 transition-colors">ม.3</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 3</div>
                </div>
              </div>
            </div>

            <div
              @click="goToPage('/learning-history-grade-m4')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-blue-500 to-sky-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-blue-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-blue-400 transition-colors">ม.4</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 4</div>
                </div>
              </div>
            </div>

            <div
              @click="goToPage('/learning-history-grade-m5')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-sky-400 to-cyan-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-sky-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-sky-400 transition-colors">ม.5</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 5</div>
                </div>
              </div>
            </div>

            <div
              @click="goToPage('/learning-history-grade-m6')"
              class="group cursor-pointer relative"
            >
              <div class="absolute inset-0 bg-gradient-to-br from-cyan-400 to-teal-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
              <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-4 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-cyan-400/50">
                <div class="text-center">
                  <div class="text-3xl font-black text-white mb-1 group-hover:text-cyan-400 transition-colors">ม.6</div>
                  <div class="text-xs text-gray-400">มัธยมศึกษาปีที่ 6</div>
                </div>
              </div>
            </div>
          </div>

          <div
            @click="goToPage('/learning-history-grade-others')"
            class="group cursor-pointer relative"
          >
            <div class="absolute inset-0 bg-gradient-to-br from-amber-400 to-yellow-400 rounded-xl blur-md opacity-40 group-hover:opacity-60 transition-all duration-300"></div>
            <div class="relative bg-slate-800/80 backdrop-blur-xl rounded-xl p-6 border border-slate-700/50 transform transition-all duration-300 hover:scale-105 hover:border-amber-400/50 h-full flex items-center justify-center">
              <div class="text-center">
                <div class="text-4xl font-black text-white mb-2 group-hover:text-amber-400 transition-colors">อื่น ๆ</div>
                <div class="text-sm text-gray-400">ระดับชั้นอื่น ๆ</div>
              </div>
            </div>
          </div>
        </div>
      </div> -->
    </div>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import { useRouter } from 'vue-router';
import BackButton from '@/components/BackButton.vue';

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