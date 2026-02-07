
<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900">
    <NavBar />

    <div class="flex items-center justify-between px-4 pt-4 pl-0 flex-wrap gap-4">
      <!-- Back Button -->
      <BackButton to="/student-management-like-tutor" class="order-1" />

      <!-- Filters + Search -->
      <div class="flex flex-1 justify-end gap-3 order-2 flex-wrap">
        <!-- Dropdown ประเภทคลาส -->
        <div class="relative">
          <select
            v-model="selectedClassType"
            @change="applyFilters"
            class="appearance-none
                  pl-11 pr-10 py-2.5
                  rounded-xl
                  bg-white/100
                  text-gray-800 font-semibold
                  shadow-md
                  border border-pink-200
                  hover:border-pink-400
                  focus:outline-none focus:ring-2 focus:ring-pink-400
                  min-w-[200px]
                  transition"
          >
            <option value="">ประเภทคลาสทั้งหมด</option>
            <option v-for="type in classTypes" :key="type" :value="type">
              {{ type }}
            </option>
          </select>

          <!-- Icon ซ้าย -->
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-pink-500">
            🎓
          </span>

          <!-- Arrow -->
          <span class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none">
            ▼
          </span>
        </div>

        <!-- Dropdown ชื่อวิชา -->
        <div class="relative mr-6">
          <select
            v-model="selectedSubject"
            @change="applyFilters"
            class="appearance-none
                  pl-11 pr-10 py-2.5
                  rounded-xl
                  bg-white/100
                  text-gray-800 font-semibold
                  shadow-md
                  border border-purple-200
                  hover:border-purple-400
                  focus:outline-none focus:ring-2 focus:ring-purple-400
                  min-w-[200px]
                  transition"
          >
            <option value="">วิชาทั้งหมด</option>
            <option v-for="subj in subjects" :key="subj" :value="subj">
              {{ subj }}
            </option>
          </select>

          <!-- Icon ซ้าย -->
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-purple-500">
            📚
          </span>

          <!-- Arrow -->
          <span class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none">
            ▼
          </span>
        </div>

        <MonthFilterDropdown 
          v-model="selectedMonths" 
          @apply="applyFilters" 
          class="min-w-[140px]"
        />
        <DateRangeFilter 
          v-model="dateRange" 
          @apply="applyFilters"
          class="min-w-[180px]"
        />
        <SearchBar
          v-model="searchKeyword"
          placeholder="ค้นหาชื่อครูหรือเนื้อหา"
          @search="applyFilters"
          class="min-w-[200px] max-w-[260px] text-teal-50"
        />
      </div>
    </div>

    <div class="w-full px-2 sm:px-4 md:px-8 lg:px-12 xl:px-16 py-8">
      <div class="text-center mb-6">
        <h1 class="text-2xl md:text-4xl font-extrabold text-white">
          ประวัติการเรียนน้อง{{ studentName }}
        </h1>
        <p class="text-lg text-gray-200 mt-2 flex items-center justify-center gap-2">
          <span class="text-white">คลาส</span>

          <span
            class="px-3 py-1 rounded-2xl
                  bg-blue-100
                  text-blue-700
                  font-semibold
                  shadow-md
                  ring-1 ring-blue-300"
          >
            {{ selectedClassType || 'ทั้งหมด' }}
          </span>
        </p>
        <button
          v-if="true"
          @click="showAddFormModal = true"
          class="mt-4 px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl hover:bg-green-700 transition-all shadow-lg flex items-center gap-2 mx-auto"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          เพิ่มฟอร์มคลาสรายชั่วโมง
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="flex justify-center items-center py-20">
        <div class="animate-spin rounded-full h-16 w-16 border-b-4 border-purple-700"></div>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="text-center text-red-500 py-8 bg-red-50 rounded-lg border border-red-200 max-w-2xl mx-auto">
        <p class="font-medium">{{ error }}</p>
      </div>

      <!-- Empty -->
      <EmptyState v-else-if="paginatedRows.length === 0" message="ไม่มีข้อมูลตรงกับเงื่อนไข" />

      <!-- Table -->
      <div v-else class="bg-white border border-black rounded-xl shadow-md overflow-x-auto">
        <table class="w-full table-auto text-sm sm:text-base">
          <thead class="bg-gradient-to-r from-purple-500 to-blue-600 text-white">
            <tr>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[160px]">วันที่ครูกรอก</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[200px]">ชื่อคลาส</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[160px]">วันที่เรียน</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[100px]">ชื่อครูผู้สอน</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[150px]">วิชา</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[200px]">เนื้อหา</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center">จำนวนชั่วโมง</th>
              <th class="px-2 sm:px-4 py-2 sm:py-3 font-semibold text-center min-w-[150px]">หมายเหตุ</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr
              v-for="row in paginatedRows"
              :key="row.uniqueKey"
              :class="[
                getRowColorClass(row),
                'transition-colors'
              ]"
            >
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center font-medium text-gray-800">{{ formatDate(row.createdAt) }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center font-semibold text-blue-700">{{ row.className || '-' }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center font-medium text-gray-800">
                {{ formatDate(row.teachingDate) }}
              </td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center text-purple-700 font-semibold">
                {{ row.tutorName || '-' }}
              </td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">
                <span :class="getSubjectBadgeClass(row.subject)">
                  {{ row.subjectName }}
                </span>
              </td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">{{ row.content }}</td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center">
                <!-- โหมดแก้ไข -->
                <div v-if="editingRowId === row.uniqueKey" class="flex flex-col items-center gap-2">
                  <!-- Input ชั่วโมง -->
                  <div class="flex items-center gap-2">
                    <label class="text-xs text-gray-600 font-medium">ชั่วโมง:</label>
                    <input
                      v-model.number="editingHours"
                      type="number"
                      step="0.5"
                      min="0"
                      class="w-20 px-2 py-1 border border-blue-400 rounded focus:ring-2 focus:ring-blue-500 text-center"
                    />
                  </div>
                  
                  <!-- Dropdown หมายเหตุ -->
                  <div class="flex items-center gap-2">
                    <label class="text-xs text-gray-600 font-medium">หมายเหตุ:</label>
                    <select
                      v-model="editingRemark"
                      class="px-2 py-1 border border-blue-400 rounded focus:ring-2 focus:ring-blue-500 text-xs"
                    >
                      <option value="">-</option>
                      <option v-for="opt in remarkOptions" :key="opt" :value="opt">
                        {{ opt }}
                      </option>
                    </select>
                  </div>
                  
                  <!-- ปุ่มบันทึก/ยกเลิก -->
                  <div class="flex gap-2 mt-1">
                    <button
                      @click="saveEdit(row)"
                      class="px-4 py-1.5 bg-green-500 text-white rounded-lg hover:bg-green-600 text-sm font-semibold shadow-md hover:shadow-lg transition-all"
                    >
                      บันทึก
                    </button>
                    <button
                      @click="cancelEdit"
                      class="px-4 py-1.5 bg-gray-400 text-white rounded-lg hover:bg-gray-500 text-sm font-semibold shadow-md hover:shadow-lg transition-all"
                    >
                      ยกเลิก
                    </button>
                  </div>
                </div>

                <!-- โหมดปกติ -->
                <div v-else class="flex items-center justify-center gap-2">
                  <span 
                    :class="[
                      'inline-flex items-center gap-1 px-3 py-1 rounded-full font-semibold',
                      parseFloat(row.hoursTaught) === 0 
                        ? 'bg-red-200 text-red-800' 
                        : 'bg-blue-100 text-blue-800'
                    ]"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    {{ row.displayHours }}
                  </span>
                  <button
                    @click="startEdit(row)"
                    class="px-3 py-1.5 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-lg hover:bg-yellow-600 text-xs font-semibold shadow-md hover:shadow-lg transition-all flex items-center gap-1"
                    title="แก้ไขชั่วโมง"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    แก้ไข
                  </button>
                  <button
                    @click="viewHistory(row.id)"
                    class="px-3 py-1.5 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:bg-purple-600 text-xs font-semibold shadow-md hover:shadow-lg transition-all flex items-center gap-1"
                    title="ดูประวัติ"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    ประวัติ
                  </button>
                </div>
              </td>
              <td class="px-2 sm:px-4 py-2 sm:py-3 text-center text-gray-600">
                {{ row.remark || '-' }}
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Summary Card -->
        <div class="bg-gradient-to-r from-purple-100 to-blue-100 px-6 py-4 border-t-2 border-purple-200">
          <div class="flex flex-wrap justify-center gap-6 text-center items-center">
            <div class="bg-white rounded-lg px-6 py-3 shadow-sm">
              <p class="text-sm text-gray-600 mb-1">จำนวนครั้งทั้งหมด</p>
              <p class="text-2xl font-bold text-purple-700">{{ validFormsCount }}</p>
            </div>
            <div class="bg-white rounded-lg px-6 py-3 shadow-sm">
              <p class="text-sm text-gray-600 mb-1">ชั่วโมงรวม</p>
              <p class="text-2xl font-bold text-blue-700">{{ totalHours }}</p>
            </div>
            <HourGroupFilter 
              v-model="hourGroup" 
              @apply="applyFilters"
            />
          </div>
        </div>

        <!-- Pagination -->
        <div class="mt-6 mb-4 flex justify-center">
          <Pagination v-model="currentPage" :totalPages="totalPages" />
        </div>
      </div>
    </div>

    <AdminCreateHourFormModal
      :show="showAddFormModal"
      :studentId="studentId"
      :studentName="studentName"
      @close="showAddFormModal = false"
      @success="handleFormCreated"
    />

    <HistoryModal
      :visible="showHistoryModal"
      :history="formHistory"
      @close="showHistoryModal = false"
    />

        <!-- Success Toast -->
    <div 
      v-if="showSuccessToast" 
      class="fixed bottom-6 right-6 bg-green-500 text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 animate-bounce z-50"
    >
      <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
      </svg>
      <span class="font-semibold">เพิ่มฟอร์มสำเร็จ!</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import BackButton from '@/components/BackButton.vue'
import SearchBar from '@/components/SearchBar.vue'
import MonthFilterDropdown from '@/components/MonthFilterDropdown.vue'
import DateRangeFilter from '@/components/DateRangeFilter.vue'
import HourGroupFilter from '@/components/HourGroupFilter.vue'
import Pagination from '@/components/Pagination.vue'
import AdminCreateHourFormModal from '@/modals/AdminCreateHourFormModal.vue'
import HistoryModal from '@/modals/HistoryModal.vue'
import api from '@/api.js'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const forms = ref([])
const loading = ref(true)
const error = ref('')
const searchKeyword = ref('')
const selectedMonths = ref([])
const dateRange = ref({ startDate: null, endDate: null })
const hourGroup = ref(null)
const currentPage = ref(1)
const pageSize = 10

const studentId = computed(() => {
  const id = route.query.studentId
  return id ? Number(id) : null
})
const studentName = computed(() => route.query.studentName || '')
const gradeParam = computed(() => route.query.grade || '')
const subjectName = computed(() => route.query.subject || '')
const editingRowId = ref(null)
const editingHours = ref(null)
const editingRemark = ref(null)
const showAddFormModal = ref(false)
const showSuccessToast = ref(false)
const showHistoryModal = ref(false)
const selectedFormId = ref(null)
const formHistory = ref([])
const isAdmin = ref(false)
const remarkOptions = [
  'ครูกรอกเกิน',
  'แอดมินนำเด็กเข้าคลาสไม่ทัน',
  'แอดมินนำเด็กออกจากคลาสไม่ทัน',
  'แก้วันเรียน',
  'น้องขอจบคอร์ส',
  'อื่น ๆ แจ้งหัวหน้า'
]

const selectedClassType = ref('')
const selectedSubject = ref('')
const allSubjects = ref([])

const loadSubjects = async () => {
  try {
    const response = await api.get('/subjects/all')
    allSubjects.value = response.data.filter(s => s.isActive)
  } catch (error) {
    console.error('Error loading subjects:', error)
  }
}

const classTypes = computed(() => {
  if (!forms.value || forms.value.length === 0) return []
  
  const types = new Set()
  forms.value.forEach(f => {
    const name = f.className || ''
    const classType = f.classType || ''
    
    // ตรวจจาก classType จาก Backend แทน
    if (classType === 'hourly_group') {
      types.add('กลุ่มรวม')
    } else if (classType === 'hourly_individual') {
      // เช็คจำนวนนักเรียนเพื่อแยก PV-เดี่ยว กับ PV-กลุ่ม
      if (name.includes('เดี่ยว') && !name.includes('กลุ่ม')) {
        types.add('PV-เดี่ยว')
      } else if (name.includes('กลุ่ม')) {
        types.add('PV-กลุ่ม')
      } else {
        types.add('PV-เดี่ยว') // default ถ้าไม่แน่ใจ
      }
    } else if (classType === 'monthly') {
      types.add('รายเดือน')
    }
    
    // Fallback: ถ้า classType ไม่ชัด ลองดูจากชื่อ
    if (name.includes('กลุ่มรวม')) types.add('กลุ่มรวม')
    else if (name.includes('รายเดือน')) types.add('รายเดือน')
  })
  return Array.from(types).sort()
})

const subjects = computed(() => {
  return allSubjects.value.map(s => s.subjectName).sort()
})

const backRoute = computed(() => `/learning-history-grade-${gradeParam.value}`)

const viewHistory = async (formId) => {
  try {
    const response = await api.get(`/hour-forms/${formId}/students/${studentId.value}/history`)
    formHistory.value = response.data
    selectedFormId.value = formId
    showHistoryModal.value = true
  } catch (error) {
    console.error('Error fetching history:', error)
    alert('ไม่สามารถโหลดประวัติได้')
  }
}

const startEdit = (row) => {
  editingRowId.value = row.uniqueKey
  editingHours.value = parseFloat(row.hoursTaught)
}

const cancelEdit = () => {
  editingRowId.value = null
  editingHours.value = null
}

const saveEdit = async (row) => {
  try {
    await api.put(`/hour-forms/${row.id}/students/${studentId.value}/hours`, {
      hours: editingHours.value,
      remark: editingRemark.value || null
    })
    
    cancelEdit()
    await fetchStudentHistory()
    
  } catch (error) {
    console.error('แก้ไขชั่วโมงล้มเหลว:', error)
    alert('แก้ไขชั่วโมงล้มเหลว: ' + (error.response?.data?.message || error.message))
  }
}

const fetchStudentHistory = async () => {
  loading.value = true
  error.value = ''

  try {
    const url = `/students/${studentId.value}/learning-history`
    const response = await api.get(url)
    forms.value = response.data || []
  } catch (err) {
    console.error('Error fetching student history:', err)
    error.value = err.response?.data?.message || 'ไม่สามารถโหลดข้อมูลได้'
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  currentPage.value = 1
}

const filteredForms = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  return forms.value
    .filter(f => {
      const matchesKeyword =
        keyword === '' ||
        ['tutorName', 'subject', 'content', 'remark'].some(k =>
          (f[k] || '').toLowerCase().includes(keyword)
        )
      
      const matchesMonth =
        selectedMonths.value.length === 0 ||
        selectedMonths.value.includes(new Date(f.teachingDate).getMonth() + 1)
      
      // กรองตามช่วงวันที่ (อิงจากวันที่กรอก)
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
      
      const wasStudentActive = f.wasStudentActive === true

      // Filter ประเภทคลาส
      const matchesClassType = selectedClassType.value === '' || (() => {
      const type = selectedClassType.value
      const classType = f.classType || ''
      const name = f.className || ''
      
      if (type === 'กลุ่มรวม') return classType === 'hourly_group'
      if (type === 'PV-เดี่ยว') return classType === 'hourly_individual' && (name.includes('เดี่ยว') && !name.includes('กลุ่ม'))
      if (type === 'PV-กลุ่ม') return classType === 'hourly_individual' && name.includes('กลุ่ม')
      if (type === 'รายเดือน') return classType === 'monthly'
      
      return true
    })()

      // Filter ชื่อวิชา
      const matchesSubject = 
        selectedSubject.value === '' || 
        f.subjectName === selectedSubject.value
      
      return matchesKeyword && matchesMonth && matchesDateRange && wasStudentActive && matchesClassType && matchesSubject
    })
})

const groupColors = [
  'bg-blue-100 hover:bg-blue-200',
  'bg-green-100 hover:bg-green-200',
  'bg-yellow-100 hover:bg-yellow-200',
  'bg-pink-100 hover:bg-pink-200',
  'bg-indigo-100 hover:bg-indigo-200',
  'bg-purple-100 hover:bg-purple-200',
  'bg-red-100 hover:bg-red-200',
  'bg-orange-100 hover:bg-orange-200',
]

const processedRows = computed(() => {
  // สร้าง snapshot ของ filteredForms เพื่อป้องกัน reactivity loop
  const snapshot = filteredForms.value.map(f => ({...f}))
  
  if (!hourGroup.value) {
    const sorted = [...snapshot].sort((a, b) => 
      new Date(b.createdAt) - new Date(a.createdAt)
    )
    return sorted.map((f, idx) => ({
      ...f,
      displayHours: String(f.hoursTaught),
      uniqueKey: `${f.id}-${idx}`,
      groupColor: null
    }))
  }

  const forCounting = [...snapshot].sort((a, b) => 
    new Date(a.createdAt) - new Date(b.createdAt)
  )

  const rows = []
  let currentGroupHours = 0
  let currentGroupIndex = 0
  let rowCounter = 0

  forCounting.forEach(form => {
    const formHours = parseFloat(form.hoursTaught) || 0
    let remainingHours = formHours

    while (remainingHours > 0) {
      const spaceInCurrentGroup = hourGroup.value - currentGroupHours
      
      if (remainingHours <= spaceInCurrentGroup) {
        rows.push({
          ...form,
          displayHours: remainingHours.toFixed(1),
          uniqueKey: `${form.id}-${rowCounter++}`,
          groupColor: groupColors[currentGroupIndex % groupColors.length],
          groupHours: currentGroupHours + remainingHours
        })
        currentGroupHours += remainingHours
        remainingHours = 0
      } else {
        rows.push({
          ...form,
          displayHours: spaceInCurrentGroup.toFixed(1),
          uniqueKey: `${form.id}-${rowCounter++}`,
          groupColor: groupColors[currentGroupIndex % groupColors.length],
          groupHours: hourGroup.value
        })
        remainingHours -= spaceInCurrentGroup
        currentGroupHours = 0
        currentGroupIndex++
      }

      if (currentGroupHours >= hourGroup.value) {
        currentGroupHours = 0
        currentGroupIndex++
      }
    }
  })

  if (currentGroupHours > 0 && currentGroupHours < hourGroup.value) {
    for (let i = rows.length - 1; i >= 0; i--) {
      if (rows[i].groupColor === groupColors[currentGroupIndex % groupColors.length]) {
        rows[i].groupColor = null
      } else {
        break
      }
    }
  }

  return rows.reverse()
})

const totalPages = computed(() => {
  const pages = Math.ceil(processedRows.value.length / pageSize)
  return pages > 0 ? pages : 1
})

const paginatedRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return processedRows.value.slice(start, start + pageSize)
})

const validFormsCount = computed(() => {
  return filteredForms.value.filter(f => parseFloat(f.hoursTaught) > 0).length
})

const totalHours = computed(() => {
  return filteredForms.value.reduce((sum, f) => sum + (parseFloat(f.hoursTaught) || 0), 0).toFixed(1)
})

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('th-TH', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getSubjectBadgeClass = () => {
  return 'px-3 py-1 bg-gray-100 text-gray-700 rounded-full font-semibold'
}

const getRowColorClass = (row) => {
  const remark = row.remark || ''
  
  // แดง
  if (remark.includes('ครูกรอกเกิน') || 
      remark.includes('แอดมินนำเด็กออกจากคลาสไม่ทัน') ||
      remark.includes('อื่น ๆ แจ้งหัวหน้า')) {
    return 'bg-red-100 hover:bg-red-200'
  }
  
  // น้ำเงิน
  if (remark.includes('น้องขอจบคอร์ส') || 
      remark.includes('แอดมินนำเด็กเข้าคลาสไม่ทัน')) {
    return 'bg-blue-100 hover:bg-blue-200'
  }
  
  // เหลือง
  if (remark.includes('แก้วันเรียน')) {
    return 'bg-yellow-100 hover:bg-yellow-200'
  }
  
  // ชั่วโมง 0
  if (parseFloat(row.hoursTaught) === 0) {
    return 'bg-red-200 hover:bg-red-300'
  }
  
  // ปกติ
  return row.groupColor || 'hover:bg-purple-50'
}

onMounted(() => {
  fetchStudentHistory()
  loadSubjects()
})

const handleFormCreated = async () => {
  showAddFormModal.value = false
  await fetchStudentHistory()

  showSuccessToast.value = true
  setTimeout(() => {
    showSuccessToast.value = false
  }, 3000)
}
</script>

<style scoped>
table {
  border-collapse: collapse;
}
</style>