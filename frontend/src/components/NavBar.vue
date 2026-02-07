<template>
  <nav class="bg-gradient-to-r from-blue-900 to-yellow-800 shadow-xl sticky top-0 z-50">
    <div class="container mx-auto px-6 py-4 flex items-center justify-between">
      <div class="flex items-center space-x-4 cursor-pointer" @click="goToHome">
        <div class="bg-black bg-opacity-20 rounded-full p-0.25 shadow-md hover:bg-opacity-40 transition-all duration-300">
          <img :src="logoImage" alt="BBP TUTOR Logo" class="w-12 h-12 object-contain" />
        </div>
        <h1 class="text-xl md:text-3xl font-bold text-white tracking-wider drop-shadow-md hover:drop-shadow-xl transition-all duration-300">
          BBP TUTOR
        </h1>
      </div>

      <div class="flex items-center space-x-4">
        <!-- Notification Bell (Admin Only) -->
        <button
          v-if="role === 'admin'"
          @click="goToNotifications"
          class="relative flex items-center justify-center rounded-lg p-2 bg-yellow-500 hover:bg-yellow-600 shadow-md transition-all duration-300 transform hover:-translate-y-0.5 group"
        >
          <svg 
            class="w-6 h-6 sm:w-7 sm:h-7 text-white group-hover:animate-wiggle" 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path 
              stroke-linecap="round" 
              stroke-linejoin="round" 
              stroke-width="2" 
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
            />
          </svg>
          
          <!-- Unread Badge -->
          <span 
            v-if="unreadCount > 0"
            class="absolute -top-1 -right-1 flex items-center justify-center w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full animate-pulse"
          >
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </button>

        <!-- List Forms Button (Tutor Only) -->
        <button
          v-if="role === 'tutor'"
          @click="goToList"
          class="flex bg-blue-600 items-center justify-center rounded-lg p-2 shadow-md transition-all duration-300 transform hover:-translate-y-0.5"
        >
          <img src="../assets/list-form.svg" alt="List Icon" class="w-6 h-6 sm:w-7 sm:h-7 invert"/>
        </button>

        <!-- Logout Button -->
        <button
          @click="handleLogout"
          class="flex items-center space-x-2 bg-red-500 hover:bg-red-600 shadow-lg hover:shadow-xl text-white px-4 py-2 rounded-lg transition-all duration-300 transform hover:-translate-y-0.5"
        >
          <svg
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
            />
          </svg>
          <span class="hidden sm:inline font-semibold">ออกจากระบบ</span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api.js';
import logoImage from '@/assets/Logo_Crop.png';

const router = useRouter();
const userName = ref('');
const role = ref('');
const unreadCount = ref(0);

const goToHome = () => {
  router.push('/home-admin');
};

onMounted(() => {
  const user = JSON.parse(
    sessionStorage.getItem('user') || localStorage.getItem('user') || '{}'
  );
  userName.value = user.username || 'ผู้ใช้';
  role.value = user.role || '';

  // Load unread notification count if admin
  if (role.value === 'admin') {
    loadUnreadCount();
    // Poll every 30 seconds
    setInterval(loadUnreadCount, 30000);
  }
});

const loadUnreadCount = async () => {
  try {
    const response = await api.get('/notifications/unread-count');
    unreadCount.value = response.data.count || 0;
  } catch (err) {
    console.error('Error loading unread count:', err);
  }
};

const goToNotifications = () => {
  router.push('/notifications');
};

const goToList = () => {
  router.push('/list-hour-forms');
};

const handleLogout = () => {
  sessionStorage.clear();
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
};
</script>

<style scoped>
@keyframes wiggle {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(-15deg);
  }
  75% {
    transform: rotate(15deg);
  }
}

.group:hover .group-hover\:animate-wiggle {
  animation: wiggle 0.5s ease-in-out;
}
</style>