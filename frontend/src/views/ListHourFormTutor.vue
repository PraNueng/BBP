<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
    <NavBar />

    <!-- Top: Back + Search (same row) -->
    <div class="flex items-center justify-between px-4 pt-4 pl-0">
      <BackButton :to="isAdminView ? '/tutor-management' : '/home-tutor'" />
      <!-- Desktop Layout - ปุ่ม Create และ DateRange -->
      <div class="hidden lg:flex lg:justify-center lg:gap-4 pl-140 md:pl-105">
        <!-- <CreateNewFormButton
          v-if="!isAdminView"
          @add="createForm"
          class="flex-shrink-0"
        /> -->
        <DateRangeFilter
          v-model="dateRange"
          @apply="applyFilters"
          class="flex-shrink-0"
        />
      </div>
      <SearchBar
        v-model="searchKeyword"
        @search="applyFilters"
        placeholder="ค้นหาชื่อคลาสหรือเนื้อหา"
        class="w-2/3 sm:w-1/3 text-white"
      />
    </div>

    <div class="container mx-auto px-4 pt-4 space-y-4">
      <!-- Mobile Layout - ปุ่ม Create และ DateRange -->
      <div class="lg:hidden flex gap-3 justify-center w-full">
        <!-- <CreateNewFormButton
          v-if="!isAdminView"
          @add="createForm"
          class="flex-1 min-w-[100px]"
        /> -->
        <DateRangeFilter
          v-model="dateRange"
          @apply="applyFilters"
          class="flex-1 min-w-[180px]"
        />
      </div>
    </div>

    <div class="w-full px-2 sm:px-4 md:px-8 lg:px-12 xl:px-16 py-8">
      <h1 class="text-2xl md:text-4xl font-extrabold text-white mb-6 text-center">
        {{ isAdminView ? `ฟอร์มที่เคยกรอกไว้ของ ${tutorNameFromQuery}` : 'ฟอร์มที่เคยกรอกไว้' }}
      </h1>

      <div v-if="formStore.loading" class="text-center text-gray-500 py-8">
        กำลังโหลดข้อมูล...
      </div>
      <div v-else-if="formStore.error" class="text-center text-red-500 py-8">
        {{ formStore.error }}
      </div>
      <EmptyState v-else-if="paginatedForms.length === 0" message="ไม่มีข้อมูลตรงกับเงื่อนไข" />

      <div v-else class="bg-white border border-gray-300 rounded-xl shadow-md overflow-x-auto">
        <table class="w-full table-auto text-sm sm:text-base">
          <thead class="bg-gradient-to-r from-blue-700 to-yellow-800 text-white">
            <tr>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[160px]">วันที่กรอก</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[100px]">ประเภท</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[200px]">ชื่อคลาส</th>
              <!-- <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">ชั้น</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">วิชา</th> -->
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[160px]">วันที่สอน</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">ชั่วโมง</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[100px]">มาเรียน</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">ขาด</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[200px]">เนื้อหา</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">หมายเหตุ</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">จัดการ</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr
              v-for="form in paginatedForms"
              :key="`${form.formType}-${form.id}`"
              :class="[
                parseFloat(form.hoursTaught) === 0 
                  ? 'bg-red-50 hover:bg-red-100' 
                  : 'hover:bg-purple-50',
                'transition-colors'
              ]"
            >
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ formatDate(form.createdAt) }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">
                <span 
                  v-if="form.formType === 'hour'" 
                  class="px-2 py-1 rounded-full text-xs font-semibold bg-yellow-100 text-yellow-800"
                >
                  รายชั่วโมง
                </span>
                <span 
                  v-else-if="form.formType === 'math'" 
                  class="px-2 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800"
                >
                  รายเดือน: คณิต
                </span>
                <span 
                  v-else-if="form.formType === 'science'" 
                  class="px-2 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800"
                >
                  รายเดือน: วิทย์
                </span>
              </td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center font-semibold text-blue-700">{{ form.className || '-' }}</td>
              <!-- <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.grade || '-' }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.subjectName }}</td> -->
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ formatDate(form.teachingDate) }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.hoursTaught }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.studentsPresent }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.studentsAbsent }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.content }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ form.remark || '-' }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3">
                <div class="flex gap-2 justify-center items-center">
                  <button
                    v-if="!isAdminView"
                    @click="goToEdit(form)"
                    class="px-4 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-lg hover:from-yellow-500 hover:to-orange-600 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    แก้ไข
                  </button>
                  
                  <!-- ปุ่มประวัติ - แสดงเฉพาะ Admin -->
                  <button
                    v-if="isAdminView"
                    @click="showHistory(form.formType, form.id)"
                    class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1 relative"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    ประวัติ
                    <span
                      v-if="form.historyCount > 0"
                      class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full border-2 border-white text-xs flex items-center justify-center"
                    >
                      {{ form.historyCount }}
                    </span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="mt-6 mb-4 flex justify-center">
          <Pagination v-model="currentPage" :totalPages="totalPages" />
        </div>
      </div>
    </div>

    <HistoryModal
      v-if="showHistoryModal"
      :visible="showHistoryModal"
      :history="selectedHistory"
      @close="showHistoryModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import BackButton from '@/components/BackButton.vue'
import SearchBar from '@/components/SearchBar.vue'
import DateRangeFilter from '@/components/DateRangeFilter.vue'
import Pagination from '@/components/Pagination.vue'
import HistoryModal from '@/modals/HistoryModal.vue'
import { useFormStore } from '@/stores/FormStore'
import EmptyState from '@/components/EmptyState.vue'
import CreateNewFormButton from '@/components/CreateNewFormButton.vue'

const router = useRouter()
const formStore = useFormStore()
const showHistoryModal = ref(false)
const selectedHistory = ref([])
const searchKeyword = ref('')
const selectedMonths = ref([])
const dateRange = ref({ startDate: null, endDate: null })
const currentPage = ref(1)
const pageSize = 10
const formType = ref('math', 'science', 'hour')
const tutorIdFromQuery = ref(null)
const tutorNameFromQuery = ref(null)
const isAdminView = ref(false)

const createForm = () => {
  if (formType.value === 'hour') router.push('/hour-forms')
}

onMounted(() => {
  const query = router.currentRoute.value.query
  
  // รับ tutorId และ tutorName จาก query params (สำหรับ admin)
  if (query.tutorId) {
    tutorIdFromQuery.value = parseInt(query.tutorId)
    tutorNameFromQuery.value = decodeURIComponent(query.tutorName || 'ครู')
    isAdminView.value = true
  }
  
  // ส่ง tutorId ไปด้วยถ้าเป็น admin mode
  formStore.fetchAllForms(tutorIdFromQuery.value)
  formType.value = query.formType
})

// onMounted(() => {
//   formStore.fetchForms()
//   const query = router.currentRoute.value.query
//   formType.value = query.formType
// })

const applyFilters = () => { currentPage.value = 1 }

const filteredForms = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return formStore.forms
    .filter(f => {
      // กรองตาม tutorId ถ้ามี (สำหรับ admin ดูประวัติครู)
      const matchesTutor = !tutorIdFromQuery.value || f.tutorId === tutorIdFromQuery.value
      
      // ค้นหาคำ - เพิ่มการค้นหา nickname ด้วย
      const matchesKeyword =
        keyword === '' ||
        ['studentName','nickname','grade','subject','content','remark','className'].some(k =>
          (f[k] || '').toLowerCase().includes(keyword)
        )
      
      // กรองตามเดือน
      const matchesMonth =
        selectedMonths.value.length === 0 ||
        selectedMonths.value.includes(new Date(f.teachingDate).getMonth() + 1)
      
      // กรองตามช่วงวันที่
      let matchesDateRange = true
      if (dateRange.value.startDate || dateRange.value.endDate) {
        const createdDate = new Date(f.createdAt)
        createdDate.setHours(0, 0, 0, 0)
        
        if (dateRange.value.startDate) {
          const start = new Date(dateRange.value.startDate)
          start.setHours(0, 0, 0, 0)
          matchesDateRange = matchesDateRange && createdDate >= start
        }
        
        if (dateRange.value.endDate) {
          const end = new Date(dateRange.value.endDate)
          end.setHours(23, 59, 59, 999)
          matchesDateRange = matchesDateRange && createdDate <= end
        }
      }
      
      return matchesTutor && matchesKeyword && matchesMonth && matchesDateRange
    })
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const totalPages = computed(() => Math.ceil(filteredForms.value.length / pageSize))
const paginatedForms = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredForms.value.slice(start, start + pageSize)
})

const formatDate = (date) => {
  if(!date) return '-'
  return new Date(date).toLocaleDateString('th-TH', { year:'numeric', month:'long', day:'numeric' })
}

const goToEdit = (form) => {
  if (form.formType === 'hour') {
    router.push(`/list-hour-form-edit/${form.id}`)
  } else if (form.formType === 'math') {
    router.push(`/list-math-form-edit/${form.id}`)
  } else if (form.formType === 'science') {
    router.push(`/list-science-form-edit/${form.id}`)
  }
}

const showHistory = async (formType, id) => {
  selectedHistory.value = []
  try {
    const history = await formStore.fetchFormHistory(formType, id)
    selectedHistory.value = history
    showHistoryModal.value = true
  } catch (err) {
    console.error("Failed to load history:", err)
    selectedHistory.value = []
    showHistoryModal.value = true
  }
}
</script>