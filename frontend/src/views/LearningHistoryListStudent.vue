<template>
  <div class="min-h-[100vh] bg-gradient-to-br from-blue-50 to-blue-100 overflow-y-auto">
    <NavBar />

    <!-- Top: Back + Search (same row) -->
    <div class="flex items-center justify-between px-4 pt-4 pl-0">
      <BackButton to="/learning-history-grades" />
      <SearchBar
        v-model="searchKeyword"
        placeholder="ค้นหาชื่อน้อง"
        class="w-2/3 sm:w-1/3"
      />
    </div>

    <div class="container mx-auto px-4 lg:px-8 py-8 max-w-7xl">
      <!-- Header -->
      <div class="relative mb-8 text-center">
        <h1 class="text-2xl sm:text-3xl font-bold text-purple-700">
          รายชื่อนักเรียนคลาส {{ gradeName }}
        </h1>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="flex justify-center items-center py-20">
        <div class="animate-spin rounded-full h-16 w-16 border-b-4 border-purple-700"></div>
      </div>

      <!-- Error -->
      <div
        v-else-if="errorMessage"
        class="bg-red-100 border border-red-400 text-red-700 px-6 py-4 rounded-lg text-center max-w-2xl mx-auto"
      >
        <p class="font-medium">{{ errorMessage }}</p>
      </div>

      <!-- Empty (no results after filtering) -->
      <div
        v-else-if="filteredStudents.length === 0"
        class="flex flex-col justify-center items-center gap-4 min-h-[40vh] text-center"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
          />
        </svg>
        <p class="text-xl text-gray-600 font-medium">ไม่พบข้อมูลนักเรียนในชั้นเรียนนี้</p>
      </div>

      <!-- Students Grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div
          v-for="student in paginatedStudents"
          :key="student.id ?? student.studentName"
          class="bg-white rounded-xl shadow-lg p-6 transform transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:bg-gradient-to-br hover:from-purple-50 hover:to-blue-50 group"
        >
          <div class="flex flex-col items-center space-y-4">
            <!-- Avatar -->
            <div class="relative">
              <div class="rounded-full bg-gradient-to-br from-purple-400 to-purple-600 w-20 h-20 flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow duration-300">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-white" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>

            <!-- Student Info -->
            <div class="text-center space-y-1">
              <h3 class="text-lg font-bold text-gray-800 group-hover:text-purple-700 transition-colors">
                {{ student.studentName }}
              </h3>
              <p class="text-sm text-gray-500">{{ gradeName }}</p>
            </div>

            <!-- Class Tabs -->
            <div class="flex justify-center gap-3">
              <!-- Math -->
              <div
                @click="goToClass('math-course', student)"
                class="flex flex-col items-center bg-red-50 hover:bg-red-100 rounded-xl px-3 py-2 cursor-pointer transition-all shadow-sm hover:shadow-md"
              >
                <div class="rounded-full bg-red-400 w-10 h-10 flex items-center justify-center mb-1">
                  <img src="../assets/math.svg" alt="Math" class="w-6 h-6 filter brightness-0" />
                </div>
                <p class="text-xs font-semibold text-red-700">คณิต</p>
              </div>

              <!-- Science -->
              <div
                @click="goToClass('science-course', student)"
                class="flex flex-col items-center bg-green-50 hover:bg-green-100 rounded-xl px-3 py-2 cursor-pointer transition-all shadow-sm hover:shadow-md"
              >
                <div class="rounded-full bg-green-400 w-10 h-10 flex items-center justify-center mb-1">
                  <img src="../assets/science.svg" alt="Science" class="w-6 h-6" />
                </div>
                <p class="text-xs font-semibold text-green-700">วิทย์</p>
              </div>

              <!-- Hour -->
              <div
                @click="goToClass('hour-forms', student)"
                class="flex flex-col items-center bg-yellow-50 hover:bg-yellow-100 rounded-xl px-3 py-2 cursor-pointer transition-all shadow-sm hover:shadow-md"
              >
                <div class="rounded-full bg-yellow-400 w-10 h-10 flex items-center justify-center mb-1">
                  <img src="../assets/clock.svg" alt="Hour" class="w-6 h-6" />
                </div>
                <p class="text-xs font-semibold text-yellow-700">ชั่วโมง</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Pagination
        v-if="totalPages >= 2"
        v-model="currentPage"
        :total-pages="totalPages"
      />
    </div>
  </div> 
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import BackButton from '@/components/BackButton.vue'
import SearchBar from '@/components/SearchBar.vue'
import api from '@/api.js'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()
const route = useRoute()
const students = ref([])
const isLoading = ref(true)
const errorMessage = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = 8

const gradeParam = computed(() => route.params.grade)

const gradeNameMap = {
  m1: 'ม.1',
  m2: 'ม.2',
  m3: 'ม.3',
  m4: 'ม.4',
  m5: 'ม.5',
  m6: 'ม.6',
  others: 'อื่น ๆ'
}
const gradeName = computed(() => gradeNameMap[gradeParam.value] || gradeParam.value)

const fetchStudents = async () => {
  isLoading.value = true
  errorMessage.value = ''
  
  try {
    const response = await api.get(`/admin/students/by-name/${gradeParam.value}`)

    // เรียงข้อมูล ก - ฮ
    const sorted = (response.data || []).sort((a, b) =>
      (a.studentName || '').localeCompare(b.studentName || '', 'th')
    )

    students.value = sorted
  } catch (error) {
    console.error('Error fetching students:', error)
    errorMessage.value =
      error.response?.status === 403
        ? 'คุณไม่มีสิทธิ์เข้าถึงข้อมูลนี้'
        : error.response?.status === 404
        ? 'ไม่พบข้อมูลนักเรียนในชั้นเรียนนี้'
        : 'เกิดข้อผิดพลาดในการโหลดข้อมูล'
  } finally {
    isLoading.value = false
  }
}

// Filter แบบเรียลไทม์ (ไม่ยิง API ใหม่)
const filteredStudents = computed(() => {
  const keyword = (searchKeyword.value || '').trim().toLowerCase()
  if (!keyword) return students.value
  
  return students.value.filter(s =>
    (s.studentName || '').toLowerCase().includes(keyword)
  )
})

// ✅ แก้ไข: ส่ง studentId และ studentName แทนที่จะส่งแค่ student
const goToClass = (path, student) => {
  let subjectName = ''
  if (path === 'math-course') subjectName = 'คณิตศาสตร์'
  else if (path === 'science-course') subjectName = 'วิทยาศาสตร์'
  else if (path === 'hour-forms') subjectName = 'รายชั่วโมง'

  const basePath = path === 'hour-forms' ? '/list-hour-form-student' : `/${path}`
  router.push({
    path: basePath,
    query: {
      studentId: student.id,           // ✅ ส่ง studentId
      studentName: student.studentName, // ✅ ส่ง studentName เพื่อแสดงผล
      grade: gradeParam.value,
      subject: subjectName,
    },
  })
}

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredStudents.value.length / pageSize))
})

const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredStudents.value.slice(start, start + pageSize)
})

// เมื่อค้นหา -> กลับหน้า 1 อัตโนมัติ
watch(searchKeyword, () => {
  currentPage.value = 1
})

// เมื่อเปลี่ยน grade -> โหลดข้อมูลใหม่
watch(gradeParam, () => {
  fetchStudents()
})

onMounted(fetchStudents)
</script>

<style scoped>
img {
  object-fit: contain;
}
.filter.brightness-0 {
  filter: brightness(0);
}
</style>