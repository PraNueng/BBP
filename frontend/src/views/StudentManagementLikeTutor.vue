<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900">
    <NavBar />
    <BackButton to="/student-management-total" class="pt-8" />

    <div class="container mx-auto px-4 lg:px-8 py-2 max-w-7xl">
      <!-- Header -->
      <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          จัดการข้อมูลนักเรียน
        </h1>
        <!-- <p class="text-purple-50 text-center mt-3 text-lg">
          จัดการคลาสและข้อมูลนักเรียนในระบบ
        </p> -->
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="mb-6 p-5 bg-gradient-to-r from-green-50 to-emerald-50 border-l-4 border-green-500 text-green-800 rounded-lg shadow-md flex items-center gap-3">
          <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="mb-6 p-5 bg-gradient-to-r from-red-50 to-pink-50 border-l-4 border-red-500 text-red-800 rounded-lg shadow-md flex items-center gap-3">
          <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ errorMessage }}</span>
        </div>

        <!-- Search & Filter -->
        <div class="mb-8 flex flex-col md:flex-row gap-4">
          <div class="flex-1">
            <SearchBar
              v-model="searchQuery"
              placeholder="ค้นหาชื่อนักเรียน"
              @search="handleSearch"
            />
          </div>
          <div>
            <select
              v-model="filterGrade"
              @change="handleFilterChange"
              class="w-full md:w-56 px-4 py-3 border-2 border-indigo-300 rounded-xl focus:ring-2 focus:ring-indigo-500 bg-white font-medium text-gray-700 shadow-sm hover:border-indigo-400 transition-all"
            >
              <option value="">ทุกชั้นเรียน</option>
              <option value="1">ม.1</option>
              <option value="2">ม.2</option>
              <option value="3">ม.3</option>
              <option value="4">ม.4</option>
              <option value="5">ม.5</option>
              <option value="6">ม.6</option>
              <option value="7">อื่น ๆ</option>
            </select>
          </div>
        </div>

        <!-- Filter Status Buttons -->
        <div class="mb-6 flex gap-2 flex-wrap">
          <button
            @click="filterStatus = 'all'"
            :class="[
              'px-4 py-2 rounded-lg font-semibold transition-all',
              filterStatus === 'all'
                ? 'bg-indigo-600 text-white shadow-lg'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            ทั้งหมด ({{ students.length }})
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>

        <!-- Students Grid -->
        <div v-else-if="filteredStudents.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="student in filteredStudents"
            :key="student.id"
            class="bg-gradient-to-br from-white to-indigo-50 border-2 border-indigo-200 rounded-xl p-6 hover:shadow-xl transition-all flex flex-col h-full"
          >
            <!-- Student Header -->
            <div class="flex items-start justify-between mb-4">
              <div class="flex items-center gap-3">
                <div class="w-14 h-14 rounded-full bg-gradient-to-r from-indigo-700 to-blue-600 flex items-center justify-center text-white">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <div>
                    <h4 class="text-xl font-bold text-gray-800">{{ student.nickname || student.firstName }}</h4>
                  <p class="text-sm text-gray-500">{{ student.firstName }} {{ student.lastName || '' }}</p>
                  <div class="flex flex-col items-start">
                    <div class="inline-block mt-1 px-2 py-1 bg-indigo-100 text-indigo-700 text-xs rounded-full font-medium">
                      {{ student.gradeName }}
                    </div>
                    <div class="inline-block mt-1 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full font-medium">
                      {{ student.schoolName || "-" }}
                    </div>
                </div>
                </div>
              </div>
              <div>
                <EditButton @edit="openEditModal(student)" class="scale-90 mt-2" />
              </div>
            </div>

            <!-- Statistics -->
            <div class="mb-4 space-y-2">
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">คลาสทั้งหมด:</span>
                <span class="font-bold text-indigo-600">
                  {{ getTotalClasses(student) }} คลาส
                </span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายเดือน:</span>
                <span class="font-bold">{{ student.totalMonthlyClasses || 0 }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายชั่วโมงแบบกลุ่ม:</span>
                <span class="font-bold">{{ student.totalHourlyGroupClasses || 0 }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-600">รายชั่วโมงแบบเดี่ยว:</span>
                <span class="font-bold">{{ student.totalHourlyIndividualClasses || 0 }}</span>
              </div>
            </div>

            <!-- Subjects -->
            <div v-if="student.subjects && student.subjects.length > 0" class="mb-4">
              <p class="text-xs text-gray-500 mb-2">วิชาที่เรียน:</p>
              <div class="flex flex-wrap gap-1">
                <span
                  v-for="subject in student.subjects"
                  :key="subject.id"
                  class="px-2 py-1 bg-indigo-100 text-indigo-700 text-xs rounded-full font-medium"
                >
                  {{ subject.name }} ({{ subject.classCount }})
                </span>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="mt-auto flex flex-col gap-2">
              <!-- ปุ่มจัดการคอร์ส -->
              <button
                @click="openManageClassesModal(student)"
                class="flex-1 py-2 bg-gradient-to-r from-blue-500 to-indigo-500 text-white rounded-lg hover:from-blue-600 hover:to-indigo-600 transition font-semibold flex items-center justify-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
                ดูคอร์สที่น้องลง
              </button>

              <!-- ปุ่มดูคลาสทั้งหมดของนักเรียน -->
              <button
                @click="openStudentDetail(student)"
                class="flex-1 py-2 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-lg hover:from-indigo-600 hover:to-purple-600 transition font-semibold flex items-center justify-center gap-2 text-center"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                ดูคลาสที่น้องลง
              </button>

              <!-- ปุ่มดูประวัติการเรียน -->
              <button
                @click="openStudentLearningHistory(student)"
                class="flex-1 py-2 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:from-purple-600 hover:to-pink-600 transition font-semibold flex items-center justify-center gap-2"
              >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
                ประวัติการเรียน
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-12">
          <svg class="mx-auto h-16 w-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <p class="mt-4 text-lg text-gray-600">ไม่พบข้อมูลนักเรียน</p>
        </div>
      </div>
    </div>

    <!-- Edit Student Modal -->
    <StudentDetail
      :show="showStudentDetail"
      :studentId="detailStudentId"
      @close="closeStudentDetail"
      @edit="handleDetailEdit"
    />
    <EditStudentModal
      :show="showEditModal"
      :student="selectedStudent"
      :isSubmitting="isSubmitting"
      :classifier-only="editModalMode === 'add-classes'"
      @close="closeEditModal"
      @success="handleUpdateSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue';
import BackButton from '@/components/BackButton.vue';
import EditButton from '@/components/EditButton.vue';
import SearchBar from '@/components/SearchBar.vue';
import EditStudentModal from '@/components/EditStudentModal.vue';
import StudentDetail from '@/modals/StudentDetail.vue'
import api from '@/api.js';

const students = ref([]);
const searchQuery = ref('');
const filterGrade = ref('');
const filterStatus = ref('all');
const isLoading = ref(false);
const isSubmitting = ref(false);
const successMessage = ref('');
const errorMessage = ref('');
const router = useRouter()
const route = useRoute();
const editModalMode = ref('edit');

// Modal state
const showEditModal = ref(false);
const selectedStudent = ref(null);

// Student detail modal state
const showStudentDetail = ref(false);
const detailStudentId = ref(null);

// Computed
const filteredStudents = computed(() => {
  let filtered = students.value;

  if (filterStatus.value === 'active') {
    filtered = filtered.filter(s => s.isActive);
  } else if (filterStatus.value === 'inactive') {
    filtered = filtered.filter(s => !s.isActive);
  }

  return filtered;
});

const openManageClassesModal = async (student) => {
  isLoading.value = true;
  try {
    const response = await api.get(`/students/${student.id}`);
    selectedStudent.value = response.data;
    editModalMode.value = 'add-classes';
    showEditModal.value = true;
  } catch (error) {
    console.error('Error loading student details:', error);
    showError('ไม่สามารถโหลดข้อมูลนักเรียนได้');
  } finally {
    isLoading.value = false;
  }
};

const openStudentDetail = (student) => {
  detailStudentId.value = student.id;
  showStudentDetail.value = true;
};

const closeStudentDetail = () => {
  showStudentDetail.value = false;
  detailStudentId.value = null;
};

// Handler when StudentDetail emits 'edit' to open the edit modal
const handleDetailEdit = (studentWithClasses) => {
  selectedStudent.value = studentWithClasses;
  editModalMode.value = 'edit';
  showEditModal.value = true;
  closeStudentDetail();
};

// Methods
const loadStudents = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/students');
    // เพิ่มข้อมูลสถิติคลาสให้แต่ละนักเรียน
    students.value = await Promise.all(
      response.data.map(async (student) => {
        try {
          const detailResponse = await api.get(`/students/${student.id}/with-classes`);
          const detail = detailResponse.data;
          
          return {
            ...student,
            totalMonthlyClasses: detail.monthlyEnrollments?.filter(e => e.isActive).length || 0,
            totalHourlyGroupClasses: detail.hourlyGroupEnrollments?.filter(e => e.isActive).length || 0,
            totalHourlyIndividualClasses: detail.hourlyIndividualClasses?.filter(c => c.isActive).length || 0,
            subjects: getUniqueSubjects(detail)
          };
        } catch (error) {
          console.warn(`Could not load details for student ${student.id}:`, error);
          return {
            ...student,
            totalMonthlyClasses: 0,
            totalHourlyGroupClasses: 0,
            totalHourlyIndividualClasses: 0,
            subjects: []
          };
        }
      })
    );
  } catch (error) {
    console.error('Error loading students:', error);
    showError('ไม่สามารถโหลดข้อมูลนักเรียนได้');
  } finally {
    isLoading.value = false;
  }
};


const getUniqueSubjects = (detail) => {
  const subjectMap = new Map();
  
  // นับจาก monthly (เฉพาะ active)
  for (const enrollment of (detail.monthlyEnrollments || [])) {
    if (enrollment.isActive) {
      const key = enrollment.subjectId;
      if (subjectMap.has(key)) {
        subjectMap.get(key).classCount++;
      } else {
        subjectMap.set(key, {
          id: enrollment.subjectId,
          name: enrollment.subjectName,
          classCount: 1
        });
      }
    }
  }
  
  // นับจาก hourly group (เฉพาะ active)
  for (const enrollment of (detail.hourlyGroupEnrollments || [])) {
    if (enrollment.isActive) {
      const key = enrollment.subjectId;
      if (subjectMap.has(key)) {
        subjectMap.get(key).classCount++;
      } else {
        subjectMap.set(key, {
          id: enrollment.subjectId,
          name: enrollment.subjectName,
          classCount: 1
        });
      }
    }
  }
  
  // นับจาก hourly individual (เฉพาะ active)
  for (const cls of (detail.hourlyIndividualClasses || [])) {
    if (cls.isActive) {
      const key = cls.subjectId;
      if (subjectMap.has(key)) {
        subjectMap.get(key).classCount++;
      } else {
        subjectMap.set(key, {
          id: cls.subjectId,
          name: cls.subjectName,
          classCount: 1
        });
      }
    }
  }
  
  return Array.from(subjectMap.values());
};

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    await loadStudents();
    return;
  }

  isLoading.value = true;
  try {
    const params = { q: searchQuery.value };
    if (filterGrade.value) {
      params.grade = filterGrade.value;
    }
    const response = await api.get('/students/search', { params });
    students.value = response.data;
  } catch (error) {
    console.error('Error searching students:', error);
    showError('ไม่สามารถค้นหาได้');
  } finally {
    isLoading.value = false;
  }
};

const handleFilterChange = async () => {
  if (filterGrade.value) {
    isLoading.value = true;
    try {
      const response = await api.get(`/students/grade/${filterGrade.value}`);
      students.value = response.data;
    } catch (error) {
      console.error('Error filtering students:', error);
      showError('ไม่สามารถกรองข้อมูลได้');
    } finally {
      isLoading.value = false;
    }
  } else {
    await loadStudents();
  }
};

const openEditModal = async (student) => {
  isLoading.value = true;
  try {
    const response = await api.get(`/students/${student.id}`);
    selectedStudent.value = response.data;
    editModalMode.value = 'edit';
    showEditModal.value = true;
  } catch (error) {
    console.error('Error loading student details:', error);
    showError('ไม่สามารถโหลดข้อมูลนักเรียนได้');
  } finally {
    isLoading.value = false;
  }
};

const closeEditModal = () => {
  showEditModal.value = false;
  selectedStudent.value = null;
};

const handleUpdateSuccess = async () => {
  showSuccess('แก้ไขข้อมูลสำเร็จ!');

  searchQuery.value = '';
  filterGrade.value = '';

  await loadStudents();
};

const getTotalClasses = (student) => {
  return (student.totalMonthlyClasses || 0) + 
         (student.totalHourlyGroupClasses || 0) + 
         (student.totalHourlyIndividualClasses || 0);
};

const showSuccess = (message) => {
  successMessage.value = message;
  errorMessage.value = '';
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const showError = (message) => {
  errorMessage.value = message;
  successMessage.value = '';
  setTimeout(() => {
    errorMessage.value = '';
  }, 5000);
};

const openStudentLearningHistory = (student) => {
  router.push({
    path: '/list-hour-form-student',
    query: {
      studentId: student.id,
      studentName: student.nickname || student.firstName,
      grade: student.gradeId,
      subject: 'ทุกวิชา'
    }
  });
};

// เช็ค query parameter เมื่อเข้าหน้า
watch(() => route.query.studentId, async (studentId) => {
  if (studentId) {
    // โหลดข้อมูลนักเรียน
    try {
      const response = await api.get(`/students/${studentId}/with-classes`);
      selectedStudent.value = response.data;
      showEditModal.value = true;
      editModalMode.value = 'add-classes'; // เปิด modal ในโหมดเพิ่มคอร์ส
      
      // Clear query parameter หลังเปิด modal
      router.replace({ query: {} });
    } catch (error) {
      console.error('Error loading student:', error);
    }
  }
}, { immediate: true });

onMounted(() => {
  loadStudents();
});
</script>

<style scoped>
@media (max-width: 640px) {
  .container {
    padding-top: 1rem;
  }
}
</style>