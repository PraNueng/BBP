
<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
    <NavBar />
    <BackButton to="/home-tutor" class="py-8" />

    <div class="container mx-auto px-4 lg:px-8 py-2 max-w-7xl">
      <h1 class="text-2xl md:text-4xl font-extrabold bg-gradient-to-r from-blue-400 to-yellow-400 bg-clip-text text-transparent mb-8 text-center">
        เลือกดูฟอร์มที่เคยกรอก
      </h1>

      <div v-if="loading" class="text-center text-gray-500 py-8">
        กำลังโหลดข้อมูล...
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-1 gap-8 justify-center max-w-75 mx-auto">
        <!-- <div
          @click="goToMathForms"
          class="cursor-pointer bg-white rounded-2xl shadow-xl p-8 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:bg-red-50"
        >
          <div class="flex flex-col items-center space-y-6">
            <div>
              <h2 class="text-xl md:text-xl font-semibold text-gray-900">
                คลาส
              </h2>
              <h1 class="text-xl md:text-4xl font-bold text-red-700 mb-2">
                คณิตศาสตร์
              </h1>
            </div>
          </div>
        </div> -->

        <!-- <div
          @click="goToScienceForms"
          class="cursor-pointer bg-white rounded-2xl shadow-xl p-8 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:bg-green-50"
        >
          <div class="flex flex-col items-center space-y-6">
            <div>
              <h2 class="text-xl md:text-xl font-semibold text-gray-900">
                คลาส
              </h2>
              <h1 class="text-xl md:text-4xl font-bold text-green-700 mb-2">
                วิทยาศาสตร์
              </h1>
            </div>
          </div>
        </div> -->

        <div
          @click="goToHourForms"
          class="group cursor-pointer relative"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-yellow-500 to-amber-500 rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-all duration-500"></div>
          <div class="relative bg-slate-800/90 backdrop-blur-xl rounded-3xl p-8 border border-slate-700/50 transform transition-all duration-500 hover:scale-105 hover:border-yellow-500/50 flex items-center justify-center max-h-45">
            <div class="flex flex-col items-center">
              <div class="relative mt-8">
                <p class="text-white font-bold text-2xl">คลาส</p>
              </div>

              <div class="text-center">
                <h2 class="text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-yellow-600 mb-4 group-hover:text-yellow-400 transition-colors duration-300 mt-5">
                  รายชั่วโมง
                </h2>
              </div>

              <div class="opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                <svg class="w-6 h-6 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- <div class="text-center mt-6 mb-6">
        <button
          @click="router.push('/home-tutor')"
          class="px-6 py-3 bg-gradient-to-r from-blue-400 to-yellow-600 text-white font-semibold rounded-lg shadow hover:shadow-lg transition duration-300"
        >
          กลับหน้าหลัก
        </button>
      </div> -->
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import BackButton from '@/components/BackButton.vue'
import api from '@/api.js'

const router = useRouter()
const allForms = ref([])
const loading = ref(false)
const error = ref(null)

onMounted(() => {
  fetchAllForms()
})

const fetchAllForms = async () => {
  loading.value = true
  error.value = null

  try {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }

    const res = await api.get('/hour-forms/my-forms');

    allForms.value = res.data || []
  } catch (err) {
    console.error('Error fetching forms:', err)
    error.value = err.message || 'เกิดข้อผิดพลาดในการดึงข้อมูล'
  } finally {
    loading.value = false
  }
}

const goToMathForms = () => {
  router.push({
    path: '/list-math-forms',
    query: { formType: 'math' }
  })
}

const goToScienceForms = () => {
  router.push({
    path: '/list-science-forms',
    query: { formType: 'science' }
  })
}

const goToHourForms = () => {
  router.push({ 
    path: '/list-hour-forms',
    query: { formType: 'hour' }
  })
}

</script>

<style scoped>
img {
  object-fit: contain;
}

.icon-black {
  filter: brightness(0) saturate(100%);
}

@media (max-width: 768px) {
  h1 {
    font-size: 2rem;
  }
  
  .grid {
    gap: 1.5rem;
  }
}
</style>