<template>
  <div class="h-screen min-h-screen blue-900 via-blue-80 flex items-center justify-center p-4 md:p-6 relative overflow-hidden">
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div
        class="absolute top-10 left-10 w-48 h-48 md:w-72 md:h-72 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
      ></div>
      <div
        class="absolute bottom-10 right-10 w-48 h-48 md:w-72 md:h-72 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
      ></div>
    </div>

    <div class="relative w-full max-w-[90%] sm:max-w-md mx-auto">
      <div
        class="absolute inset-0 bg-gradient-to-r from-yellow-400 to-blue-900 rounded-3xl blur-xl opacity-25"
      ></div>

      <div class="relative bg-white bg-opacity-15 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-400 border-opacity-40 p-5 sm:p-8 md:p-10">
        <!-- Logo -->
        <div class="flex justify-center mt-3">
          <div class="bg-gradient-to-br from-yellow-400 to-yellow-600 rounded-2xl p-3 shadow-lg">
            <img 
              :src="logoImage" 
              alt="BBP TUTOR Logo" 
              class="w-16 h-16 md:w-20 md:h-20 object-contain"
            />
          </div>
        </div>

        <div class="text-center mb-8 mt-4">
          <h1 class="text-4xl md:text-5xl font-bold mb-2 tracking-wide bg-gradient-to-r from-blue-700 to-blue-500 bg-clip-text text-transparent drop-shadow-lg" style="font-family: 'Kanit', 'Sarabun', sans-serif; letter-spacing: 0.05em;">
            BBP TUTOR
          </h1>
        </div>

        <div
          v-if="errorMessage"
          class="mb-6 bg-red-500 bg-opacity-20 backdrop-blur-sm border border-red-400 border-opacity-40 rounded-xl p-4 animate-shake"
        >
          <div class="flex items-center">
            <svg
              class="w-5 h-5 text-red-200 mr-3 flex-shrink-0"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                clip-rule="evenodd"
              />
            </svg>
            <span class="text-red-100 text-sm md:text-base">{{ errorMessage }}</span>
          </div>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- Username -->
          <div class="group">
            <label
              class="block text-gray-500 text-sm md:text-base font-semibold mb-3 ml-1"
              >Username</label
            >
            <div class="relative">
              <div
                class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none"
              >
                <svg
                  class="w-5 h-5 md:w-6 md:h-6 text-gray-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <input
                v-model="username"
                type="text"
                required
                class="w-full pl-8 md:pl-8 pr-4 py-3.5 md:py-4 bg-white bg-opacity-20 border-2 border-gray-400 border-opacity-50 rounded-xl text-black text-base md:text-lg placeholder-gray-300 focus:outline-none focus:border-yellow-400 focus:bg-white focus:bg-opacity-30 transition-all duration-200 backdrop-blur-sm"
                placeholder="กรอกชื่อผู้ใช้"
              />
            </div>
          </div>

          <!-- Password -->
          <div class="group">
            <label
              class="block text-gray-500 text-sm md:text-base font-semibold mb-3 ml-1"
              >Password</label
            >
            <div class="relative">
              <div
                class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none"
              >
                <svg
                  class="w-5 h-5 md:w-6 md:h-6 text-gray-300"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                  />
                </svg>
              </div>
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                class="w-full pl-8 md:pl-8 pr-12 md:pr-14 py-3.5 md:py-4 bg-white bg-opacity-20 border-2 border-gray-400 border-opacity-50 rounded-xl text-black text-base md:text-lg placeholder-gray-300 focus:outline-none focus:border-yellow-400 focus:bg-white focus:bg-opacity-30 transition-all duration-200 backdrop-blur-sm"
                placeholder="กรอกรหัสผ่าน"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-300 hover:text-yellow-400 transition-colors duration-200"
              >
                <svg
                  v-if="!showPassword"
                  class="w-5 h-5 md:w-6 md:h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                  />
                </svg>
                <svg
                  v-else
                  class="w-5 h-5 md:w-6 md:h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M3 3l3.59 3.59A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7"
                  />
                </svg>
              </button>
            </div>
          </div>

          <!-- Button -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full relative group overflow-hidden rounded-xl py-4 md:py-4.5 font-semibold text-lg md:text-xl shadow-xl transition-all duration-200 active:scale-95 disabled:opacity-50 mt-8"
          >
            <div
              class="absolute inset-0 bg-gradient-to-r from-yellow-400 via-yellow-500 to-yellow-600"
            ></div>
            <span class="relative text-blue-900 flex items-center justify-center">
              <span v-if="!isLoading">เข้าสู่ระบบ</span>
              <span v-else class="flex items-center">
                <svg
                  class="animate-spin -ml-1 mr-3 h-6 w-6 text-blue-900"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                กำลังเข้าสู่ระบบ...
              </span>
            </span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
html, body {
  height: 100%;
  overflow-x: hidden;
}

#app {
  min-height: 100svh;
  display: flex;
  align-items: center;
}

/* Force minimum sizes for mobile */
@media screen and (max-width: 640px) {
  .min-h-screen {
    min-height: 100svh;
    display: flex;
    align-items: center;
  }
  
  input, button {
    min-height: 48px; /* iOS touch target minimum */
  }
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-5px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(5px);
  }
}

.animate-shake {
  animation: shake 0.5s;
}
</style>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import logoImage from '@/assets/Logo_Crop.png';
import api from '@/api.js';

const router = useRouter();
const username = ref('');
const password = ref('');
const showPassword = ref(false);
const rememberMe = ref(false);
const isLoading = ref(false);
const errorMessage = ref('');

const handleLogin = async () => {
  errorMessage.value = '';

  if (username.value !== username.value.trim()) {
    errorMessage.value = 'Username หรือ Password ผิด';
    return;
  }

  isLoading.value = true;

  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    });

    const { token, user } = response.data;
    
    if (rememberMe.value) {
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      sessionStorage.setItem('token', token);
      sessionStorage.setItem('user', JSON.stringify(user));
    }

    if (user.role === 'tutor') {
      router.push('/home-tutor');
    } else if (user.role === 'admin') {
      router.push('/home-admin');
    }

  } catch (error) {
    if (error.response?.data?.message) {
      errorMessage.value = 'Username หรือ Password ผิด';
    } else if (error.response?.status === 400) {
      errorMessage.value = 'Username หรือ Password ผิด';
    } else {
      errorMessage.value = 'ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>