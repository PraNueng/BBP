<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-indigo-900 overflow-y-auto relative">
    <!-- Animated Background -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute w-96 h-96 bg-blue-500/20 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
      <div class="absolute w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse delay-1000"></div>
    </div>

    <NavBar />
    <BackButton to="/student-management-total" class="pt-8" />

    <div class="container mx-auto px-4 lg:px-8 py-2 max-w-7xl relative z-10">
      <!-- Header -->
      <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          เพิ่มข้อมูลนักเรียนใหม่ในระบบ
        </h1>
        <!-- <p class="text-blue-100 text-center mt-3 text-lg">
          เพิ่มข้อมูลนักเรียนใหม่ในระบบ
        </p> -->
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- Add Student Form -->
        <div class="mb-8 p-8 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl border-2 border-blue-300 shadow-md">
          <div class="flex flex-row justify-between">
          <h2 class="text-2xl font-bold text-blue-800 mb-6 flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
            เพิ่มนักเรียนใหม่
          </h2>
        </div>
          
          <form @submit.prevent="handleAddStudent" class="grid grid-cols-1 md:grid-cols-4 gap-4">

          <!-- ชื่อ -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">ชื่อ<span class="text-red-500">*</span></label>
            <input
              v-model="newStudent.firstName"
              type="text"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="กรอกชื่อจริงของน้อง"
            />
            <p v-if="validationErrors.firstName" class="mt-1 text-sm text-red-600">
              {{ validationErrors.firstName }}
            </p>
          </div>

          <!-- นามสกุล -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">นามสกุล</label>
            <input
              v-model="newStudent.lastName"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="กรอกนามสกุลของน้อง"
            />
            <p v-if="validationErrors.lastName" class="mt-1 text-sm text-red-600">
              {{ validationErrors.lastName }}
            </p>
          </div>

          <!-- ชื่อเล่น -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">ชื่อเล่น<span class="text-red-500">*</span></label>
            <input
              v-model="newStudent.nickname"
              type="text"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="กรอกชื่อเล่นของน้อง"
            />
            <p v-if="validationErrors.nickname" class="mt-1 text-sm text-red-600">
              {{ validationErrors.nickname }}
            </p>
          </div>

          <!-- โรงเรียน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">โรงเรียน</label>
            <input
              v-model="newStudent.schoolName"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="เช่น บางปะกอกวิทยาคม"
            />
          </div>

          <!-- ชั้นเรียน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">ชั้นเรียน <span class="text-red-500">*</span></label>
            <select
              v-model="newStudent.gradeId"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option :value="null">-- เลือกชั้นเรียน --</option>
              <option :value="1">ม.1</option>
              <option :value="2">ม.2</option>
              <option :value="3">ม.3</option>
              <option :value="4">ม.4</option>
              <option :value="5">ม.5</option>
              <option :value="6">ม.6</option>
              <option :value="7">อื่น ๆ</option>
            </select>
            <p v-if="validationErrors.gradeId" class="mt-1 text-sm text-red-600">
              {{ validationErrors.gradeId }}
            </p>
          </div>

          <!-- แผนการเรียน -->
           <div>
            <label class="block text-gray-700 font-semibold mb-2">แผนการเรียน</label>
            <textarea
              v-model="newStudent.studyPlan"
              rows="1"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="เช่น วิทย์-คณิต, ศิลป์-ภาษา"
            >
            </textarea>
          </div>

          <!-- เบอร์นักเรียน -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">เบอร์นักเรียน</label>
            <input
              v-model="newStudent.phoneStudent"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="09xxxxxxxx"
            />
            <p v-if="validationErrors.phoneStudent" class="mt-1 text-sm text-red-600">
              {{ validationErrors.phoneStudent }}
            </p>
          </div>

          <!-- เบอร์ผู้ปกครอง -->
          <div>
            <label class="block text-gray-700 font-semibold mb-2">เบอร์ผู้ปกครอง</label>
            <input
              v-model="newStudent.phoneParent"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              placeholder="09xxxxxxxx"
            />
            <p v-if="validationErrors.phoneParent" class="mt-1 text-sm text-red-600">
              {{ validationErrors.phoneParent }}
            </p>
          </div>

          <!-- คลาสเรียน (Optional) -->
          <div class="md:col-span-4">
            <StudentCoursePurchaseSelector 
              v-model="newStudent.coursePurchases" 
              :showHistoryButton="false"
              @validate="validateCoursePurchases" 
              @duplicate-error="handleDuplicateError"
            />
            <p v-if="classValidationError" class="mt-2 text-sm text-blue-600">
              {{ classValidationError }}
            </p>
          </div>
                    <!-- ปุ่มตกลง -->
          <div class="md:col-span-4 flex justify-center">
            <button
              @click="handleAddStudent"
              :disabled="isSubmitting || isSubmitDisabled"
              class="px-6 py-2 bg-gradient-to-r from-green-500 to-green-700 text-white font-bold text-lg rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed transform hover:scale-105"
            >
              <span v-if="!isSubmitting" class="flex items-center gap-2">
                ยืนยัน
              </span>
              <span v-else class="flex items-center gap-2">
                <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                กำลังบันทึก...
              </span>
            </button>
          </div>
        </form>

          <!-- Success/Error Messages -->
          <div :class="['fixed right-6 bottom-6 flex flex-col items-end gap-3', showEditModal || showDetailModal ? 'z-[60]' : 'z-40']" aria-live="polite">
            <transition-group name="slide-fade" tag="div">
              <div v-if="successMessage" key="success" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-green-500 to-emerald-600 text-white shadow-lg flex items-center gap-4">
                <CheckCircle class="w-7 h-7" />
                <div class="text-base font-semibold">{{ successMessage }}</div>
              </div>
              <div v-if="errorMessage" key="error" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-red-500 to-pink-600 text-white shadow-lg flex items-center gap-4">
                <AlertCircle class="w-7 h-7" />
                <div class="text-base font-semibold">{{ errorMessage }}</div>
              </div>
              <div v-if="duplicateCourseError" key="duplicate" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-red-500 to-pink-600 text-white shadow-lg flex items-center gap-4">
                <AlertCircle class="w-7 h-7" />
                <div class="text-base font-semibold">{{ duplicateCourseError }}</div>
              </div>
            </transition-group>
          </div>
        </div>

        <!-- Filters -->
        <div class="mb-8 flex flex-col md:flex-row gap-4 bg-gradient-to-r from-gray-50 to-blue-50 p-6 rounded-2xl shadow-md border border-blue-200">
          <div class="flex-1">
            <SearchBar
              v-model="searchQuery"
              placeholder="ค้นหาชื่อนักเรียน"
              @search="handleSearch"
              @update:modelValue="handleSearch"
            />
          </div>
          <div>
            <select
              v-model="filterGrade"
              @change="handleFilterChange"
              class="w-full md:w-56 px-4 py-3 border-2 border-blue-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white font-medium text-gray-700 shadow-sm hover:border-indigo-400 transition-all"
            >
              <option :value="''">ทุกชั้นเรียน</option>
              <option :value="1">ม.1</option>
              <option :value="2">ม.2</option>
              <option :value="3">ม.3</option>
              <option :value="4">ม.4</option>
              <option :value="5">ม.5</option>
              <option :value="6">ม.6</option>
              <option :value="7">อื่น ๆ</option>
            </select>
          </div>
        </div>

        <!-- Students Table -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>

        <div v-else-if="students.length === 0" class="text-center py-12">
          <p class="text-gray-500 text-lg">ไม่พบข้อมูลนักเรียน</p>
        </div>

        <div v-else class="overflow-x-auto rounded-xl shadow-lg">
          <table class="w-full">
            <thead class="bg-gradient-to-r from-blue-600 to-indigo-600">
              <tr class="text-center">
                <th class="px-6 py-4 text-sm font-bold text-white">ลำดับที่</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ชื่อเล่น</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ชื่อ-สกุล</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ชั้นเรียน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">วันที่เพิ่ม</th>
                <th class="px-6 py-4 text-sm font-bold text-white">จัดการ</th>
              </tr>
            </thead>
              <tbody class="divide-y divide-gray-100 bg-white">
                <tr 
                  v-for="(student, index) in paginatedStudents" 
                  :key="student.id" 
                  @click="openDetailModal(student.id)"
                  class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-indigo-50 transition-all cursor-pointer text-center border-b border-gray-50"
                >
                  <td class="px-6 py-4 text-sm text-gray-700 font-medium">
                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </td>
                  <td class="px-6 py-4 text-sm font-semibold text-blue-600">
                    {{ student.nickname || '-' }}
                  </td>
                  <td class="px-6 py-4 text-sm font-medium text-gray-800">
                    {{ student.fullName || student.firstName }}
                  </td>
                  <td class="px-6 py-4 text-sm text-gray-700">
                    <span class="px-3 py-1 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-full font-medium text-xs shadow-sm">
                      {{ student.gradeName || '-' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-sm text-gray-600">
                    {{ formatDate(student.createdAt) }}
                  </td>
                  <td class="px-6 py-4 text-center" @click.stop>
                    <div class="flex justify-center gap-2">
                      <button
                        @click="openDetailModal(student.id)"
                        class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                        ข้อมูล
                      </button>
                      <button
                        @click="openHistoryModal(student)"
                        class="px-4 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        ประวัติ
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
          </table>

          <StudentHistoryModal
            :visible="showHistoryModal"
            :history="studentHistory"
            :loading="loadingHistory"
            :studentName="selectedStudentName"
            :nickname="selectedStudentNickname"
            :grade="selectedStudentGrade"
            @close="showHistoryModal = false"
          />

          <!-- Pagination -->
          <div v-if="students.length > 0" class="mt-8">
            <Pagination
              v-model="currentPage"
              :totalPages="totalPages"
            />
          </div>
        </div>
      </div>
    </div>

    <EditStudentModal
      :show="showEditModal"
      :student="selectedStudent"
      :isSubmitting="isSubmitting"
      @close="closeEditModal"
      @success="handleUpdateSuccess"
    />

    <StudentDetail
      :show="showDetailModal"
      :studentId="selectedStudentId"
      @close="closeDetailModal"
      @edit="handleEditFromDetail"
    />

  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue';
import { CheckCircle, AlertCircle } from 'lucide-vue-next';
import NavBar from '@/components/NavBar.vue';
import api from '@/api.js';
import BackButton from '@/components/BackButton.vue';
import SearchBar from '@/components/SearchBar.vue';
import EditStudentModal from '@/components/EditStudentModal.vue';
import Pagination from '@/components/Pagination.vue';
import StudentHistoryModal from '@/modals/StudentHistoryModal.vue';
import StudentCoursePurchaseSelector from '@/components/StudentCoursePurchaseSelector.vue';
import StudentDetail from '@/modals/StudentDetail.vue';

const isLoading = ref(false);
const isSubmitting = ref(false);
const students = ref([]);
const searchQuery = ref('');
const filterGrade = ref('');
const successMessage = ref('');
const errorMessage = ref('');
const selectedStudent = ref(null);
const currentPage = ref(1);
const itemsPerPage = 10;
const showHistoryModal = ref(false);
const studentHistory = ref([]);
const loadingHistory = ref(false);
const selectedStudentName = ref('');
const selectedStudentNickname = ref('');
const selectedStudentGrade = ref('');
const classValidationError = ref('');
const showDetailModal = ref(false);
const selectedStudentId = ref(null);
const duplicateCourseError = ref('');
const subjects = ref([]);

const loadSubjects = async () => {
  try {
    const response = await api.get('/subjects/all');
    subjects.value = response.data.filter(item => item.isActive);
  } catch (error) {
    console.error('Error loading subjects:', error);
  }
};

const openDetailModal = (studentId) => {
  selectedStudentId.value = studentId;
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedStudentId.value = null;
};

const handleEditFromDetail = async (student) => {
  closeDetailModal();
  await openEditModal(student);
};

const openHistoryModal = async (student) => {
  selectedStudentName.value = student.fullName || student.firstName;
  selectedStudentNickname.value = student.nickname || '';
  selectedStudentGrade.value = student.gradeName || '';
  showHistoryModal.value = true;
  loadingHistory.value = true;
  try {
    const res = await api.get(`/students/${student.id}/history`);
    studentHistory.value = res.data || [];
  } catch (error) {
    console.error('Error loading history:', error);
    studentHistory.value = [];
  } finally {
    loadingHistory.value = false;
  }
};

const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return students.value.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => Math.ceil(students.value.length / itemsPerPage));

const newStudent = ref({
  firstName: '',
  lastName: '',
  nickname: '',
  schoolName: '',
  gradeId: null,
  phoneStudent: '',
  phoneParent: '',
  studyPlan: '',
  coursePurchases: []
});

const showEditModal = ref(false);

const loadStudents = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/students');
    students.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error loading students:', error);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลนักเรียนได้';
    students.value = [];
  } finally {
    isLoading.value = false;
  }
};

const isSubmitDisabled = computed(() => {
  if (!newStudent.value.firstName || !newStudent.value.nickname || !newStudent.value.gradeId) {
    return true;
  }
  
  if (newStudent.value.coursePurchases && newStudent.value.coursePurchases.length > 0) {
    return newStudent.value.coursePurchases.some(course => !course.confirmed);
  }
  
  return false;
});

const handleSearch = async () => {
  isLoading.value = true;
  try {
    const params = { q: searchQuery.value };
    if (filterGrade.value) {
      params.grade = filterGrade.value;
    }
    const response = await api.get('/students/search', { params });
    students.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('Error searching students:', error);
    students.value = [];
  } finally {
    isLoading.value = false;
  }
};

const handleFilterChange = async () => {
  if (filterGrade.value) {
    isLoading.value = true;
    try {
      const response = await api.get(`/students/grade/${filterGrade.value}`);
      students.value = Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      console.error('Error filtering students:', error);
      students.value = [];
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
    const fullStudent = response.data;
    
    selectedStudent.value = {
      id: fullStudent.id,
      firstName: fullStudent.firstName || '',
      lastName: fullStudent.lastName || '',
      nickname: fullStudent.nickname || '',
      schoolName: fullStudent.schoolName || '',
      gradeId: fullStudent.gradeId || '',
      phoneStudent: fullStudent.phoneStudent || '',
      phoneParent: fullStudent.phoneParent || '',
      studyPlan: fullStudent.studyPlan || ''
    };
    
    showEditModal.value = true;
  } catch (error) {
    console.error('Error loading student details:', error);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลนักเรียนได้';
  } finally {
    isLoading.value = false;
  }
};

const closeEditModal = () => {
  showEditModal.value = false;
  selectedStudent.value = null;
};

const handleUpdateSuccess = async () => {
  successMessage.value = 'แก้ไขข้อมูลสำเร็จ!';
  await loadStudents();
  
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const handleUpdateStudent = async (updatedData) => {
  isSubmitting.value = true;
  try {
    // กรอง classes ที่ไม่สมบูรณ์ออก
    const validClasses = updatedData.classes?.filter(cls => {
      if (!cls.subjectId) return false;
      if (!cls.classType || !cls.mode) return false;
      if (cls.classType === 'MONTH' && cls.mode === 'GROUP' && !cls.schedule) return false;
      if (cls.classType === 'HOUR' && cls.mode === 'GROUP' && !cls.groupType) return false;
      if (cls.classType === 'HOUR' && (!cls.hours || cls.hours <= 0)) return false;
      return true;
    }) || [];

    const processedClasses = validClasses.map(cls => ({
      ...cls,
      subjectId: parseInt(cls.subjectId),
      hours: cls.hours ? parseInt(cls.hours) : null,
      tutorId: cls.tutorId ? parseInt(cls.tutorId) : null
    }));

    const payload = {
      id: updatedData.id,
      firstName: updatedData.firstName,
      lastName: updatedData.lastName,
      nickname: updatedData.nickname,
      schoolName: updatedData.schoolName,
      gradeId: parseInt(updatedData.gradeId),
      phoneStudent: updatedData.phoneStudent,
      phoneParent: updatedData.phoneParent,
      studyPlan: updatedData.studyPlan
    };

    if (processedClasses.length > 0) {
      payload.classes = processedClasses;
    }

    await api.put(`/students/${updatedData.id}`, payload);
    
    successMessage.value = 'แก้ไขข้อมูลสำเร็จ!';
    closeEditModal();
    await loadStudents();
    
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (error) {
    console.error('Error updating student:', error);
    
    // แสดง error message จาก Backend
    if (error.response?.data?.message) {
      // Backend ส่ง message มา เช่น "ไม่พบคลาสดังกล่าว กรุณาตรวจสอบว่ามีครูผู้สอนหรือไม่"
      errorMessage.value = error.response.data.message;
    } else if (error.response?.status === 400) {
      errorMessage.value = 'ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง';
    } else if (error.response?.status === 404) {
      errorMessage.value = 'ไม่พบข้อมูลที่ต้องการ';
    } else {
      errorMessage.value = 'เกิดข้อผิดพลาดในการแก้ไขข้อมูล';
    }
    
    setTimeout(() => {
      errorMessage.value = '';
    }, 5000);
  } finally {
    isSubmitting.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('th-TH', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

const validationErrors = ref({
  firstName: '',
  lastName: '',
  nickname: '',
  phoneStudent: '',
  phoneParent: '',
  gradeId: ''
});

const PHONE_PATTERN = /^0\d{9}$/;
const NAME_PATTERN = /^[ก-๙A-Za-z ]+$/;

const validateField = (field, value) => {
  let error = '';
  
  switch(field) {
    case 'firstName':
      if (!value || value.trim() === '') {
        error = 'กรุณากรอกชื่อ';
      } else if (value.length > 100) {
        error = 'ชื่อยาวเกินไป (สูงสุด 100 ตัวอักษร)';
      } else if (!NAME_PATTERN.test(value)) {
        error = 'ชื่อประกอบด้วยตัวอักษรไทยหรืออังกฤษเท่านั้น';
      }
      break;

    case 'lastName':
      if (value && value.trim() !== '') {
        if (value.length > 100) {
          error = 'นามสกุลยาวเกินไป (สูงสุด 100 ตัวอักษร)';
        } else if (!NAME_PATTERN.test(value)) {
          error = 'นามสกุลประกอบด้วยตัวอักษรไทยหรืออังกฤษเท่านั้น';
        }
      }
      break;
      
    case 'nickname':
      if (!value || value.trim() === '') {
        error = 'กรุณากรอกชื่อเล่น';
      } else if (value.length > 50) {
        error = 'ชื่อเล่นยาวเกินไป (สูงสุด 50 ตัวอักษร)';
      }
      break;
      
    case 'gradeId':
      if (!value) {
        error = 'กรุณาเลือกชั้นเรียน';
      }
      break;
      
    case 'phoneStudent':
    case 'phoneParent':
      if (value && value.trim() !== '') {
        if (!PHONE_PATTERN.test(value)) {
          error = 'เบอร์โทรไม่ถูกต้อง (ต้องขึ้นต้นด้วย 0 และมี 10 หลัก)';
        }
      }
      break;
  }
  
  validationErrors.value[field] = error;
  return error === '';
};

const validateClasses = () => {
  classValidationError.value = '';
  
  if (!newStudent.value.classes || newStudent.value.classes.length === 0) {
    return true; // Classes are optional
  }

  // ตรวจสอบแต่ละคลาส
  for (let i = 0; i < newStudent.value.classes.length; i++) {
    const cls = newStudent.value.classes[i];

    if (!cls.subjectId) {
      classValidationError.value = `คลาสที่ ${i + 1}: กรุณาเลือกวิชา`;
      return false;
    }
    
    if (!cls.classType || !cls.mode) {
      classValidationError.value = `คลาสที่ ${i + 1}: กรุณาเลือกประเภทและโหมด`;
      return false;
    }
    
    if (cls.classType === 'MONTH' && cls.mode === 'GROUP' && !cls.schedule) {
      classValidationError.value = `คลาสที่ ${i + 1}: กรุณาเลือกเวลาเรียน`;
      return false;
    }
    
    if (cls.classType === 'HOUR' && cls.mode === 'GROUP' && !cls.groupType) {
      classValidationError.value = `คลาสที่ ${i + 1}: กรุณาเลือกประเภทกลุ่ม`;
      return false;
    }
    
    if (cls.classType === 'HOUR' && (!cls.hours || cls.hours <= 0)) {
      classValidationError.value = `คลาสที่ ${i + 1}: กรุณาใส่จำนวนชั่วโมง`;
      return false;
    }
  }
  
  return true;
};

const validateForm = () => {
  let isValid = true;
  
  isValid = validateField('firstName', newStudent.value.firstName) && isValid;
  isValid = validateField('lastName', newStudent.value.lastName) && isValid;
  isValid = validateField('nickname', newStudent.value.nickname) && isValid;
  isValid = validateField('gradeId', newStudent.value.gradeId) && isValid;
  isValid = validateField('phoneStudent', newStudent.value.phoneStudent) && isValid;
  isValid = validateField('phoneParent', newStudent.value.phoneParent) && isValid;
  
  return isValid;
};

const handleAddStudent = async () => {
  duplicateCourseError.value = '';

  if (!validateForm()) {
    errorMessage.value = 'กรุณาตรวจสอบข้อมูลที่กรอกให้ถูกต้อง';
    setTimeout(() => {
      errorMessage.value = '';
    }, 5000);
    return;
  }
  
  if (!validateCoursePurchases()) {
    return;
  }
  
  isSubmitting.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  try {
    // กรองคอร์สที่สมบูรณ์
    const validCourses = newStudent.value.coursePurchases?.filter(course => {
      if (!course.subjectId || !course.classType) return false;
      
      // ถ้าเป็น MONTHLY ต้องมี monthlySubtypeId
      if (course.classType === 'MONTHLY') {
        return !!course.monthlySubtypeId;
      }
      
      // ถ้าไม่ใช่ MONTHLY ต้องมี hoursPurchased
      return course.hoursPurchased && course.hoursPurchased > 0;
    }) || [];

    // แปลงเป็นตัวเลข
    const processedCourses = validCourses.map(course => ({
      subjectId: parseInt(course.subjectId),
      hoursPurchased: course.hoursPurchased ? parseFloat(course.hoursPurchased) : null,
      classType: course.classType || null,
      monthlySubtypeId: course.monthlySubtypeId ? parseInt(course.monthlySubtypeId) : null
    }));

    const payload = {
      firstName: newStudent.value.firstName,
      lastName: newStudent.value.lastName,
      nickname: newStudent.value.nickname,
      schoolName: newStudent.value.schoolName,
      gradeId: parseInt(newStudent.value.gradeId),
      phoneStudent: newStudent.value.phoneStudent,
      phoneParent: newStudent.value.phoneParent,
      studyPlan: newStudent.value.studyPlan
    };

    // ถ้ามีคอร์สที่ถูกต้อง ให้เพิ่มเข้าไปใน payload
    if (processedCourses.length > 0) {
      payload.coursePurchases = processedCourses;
      console.log('📦 Adding course purchases to payload:', processedCourses.length, 'courses');
    } else {
      console.log('⚠️ No valid course purchases to add');
    }

    const response = await api.post('/students', payload);
    
    successMessage.value = 'เพิ่มนักเรียนสำเร็จ!';
    
    await loadStudents();
    await nextTick();
    
    // Reset form
    newStudent.value = {
      firstName: '',
      lastName: '',
      nickname: '',
      schoolName: '',
      gradeId: null,
      phoneStudent: '',
      phoneParent: '',
      studyPlan: '',
      coursePurchases: []
    };

    validationErrors.value = {
      firstName: '',
      lastName: '',
      nickname: '',
      phoneStudent: '',
      phoneParent: '',
      gradeId: ''
    };

    classValidationError.value = '';
    
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
    
  } catch (error) {
    console.error('=== ERROR CREATE STUDENT ===');
    console.error('Error object:', error);
    console.error('Response status:', error.response?.status);
    console.error('Response data:', error.response?.data);

    if (error.response?.status === 403) {
      errorMessage.value = 'มีนักเรียนชื่อนี้ในชั้นเรียนนี้อยู่แล้ว';
    } else {
      errorMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการเพิ่มนักเรียน';
    }
    setTimeout(() => {
      errorMessage.value = '';
    }, 5000);
  } finally {
    isSubmitting.value = false;
  }
};

const handleDuplicateError = (duplicateInfo) => {
  const subjectName = subjects.value.find(
    s => s.id === duplicateInfo.subjectId
  )?.subjectName || 'วิชานี้';
  
  let classTypeText;
  switch (duplicateInfo.classType) {
    case 'GROUP':
      classTypeText = 'กลุ่มรวม';
      break;
    case 'INDIVIDUAL':
      classTypeText = 'PV-เดี่ยว';
      break;
    case 'INDIVIDUAL_GROUP':
      classTypeText = 'PV-กลุ่ม';
      break;
    default:
      classTypeText = 'ไม่ทราบประเภท';
  }
  
  duplicateCourseError.value = `มีคอร์ส${subjectName} ประเภท${classTypeText}ซ้ำกันในคอร์สที่ ${duplicateInfo.index1 + 1} และ ${duplicateInfo.index2 + 1}\nกรุณาแก้ไขประเภทคลาสให้ถูกต้อง`;
  
  setTimeout(() => {
    duplicateCourseError.value = '';
  }, 5000);
};

// Validation สำหรับคอร์ส
const validateCoursePurchases = () => {
  classValidationError.value = '';
  
  if (!newStudent.value.coursePurchases || newStudent.value.coursePurchases.length === 0) {
    return true; // Course purchases are optional
  }

  // ตรวจสอบแต่ละคอร์ส
  for (let i = 0; i < newStudent.value.coursePurchases.length; i++) {
    const course = newStudent.value.coursePurchases[i];

    if (!course.subjectId) {
      classValidationError.value = `คอร์สที่ ${i + 1}: กรุณาเลือกวิชา`;
      return false;
    }
    
    if (course.classType !== 'MONTHLY' && (!course.hoursPurchased || course.hoursPurchased <= 0)) {
      classValidationError.value = `คอร์สที่ ${i + 1}: กรุณาใส่จำนวนชั่วโมงที่ซื้อ`;
      return false;
    }

    if (course.classType === 'MONTHLY' && !course.monthlySubtypeId) {
      classValidationError.value = `คอร์สที่ ${i + 1}: กรุณาเลือกกลุ่มรายเดือน`;
      return false;
    }

    if (!course.classType) {
      classValidationError.value = `คอร์สที่ ${i + 1}: กรุณาเลือกประเภทคลาส`;
      return false;
    }
  }
  
  return true;
};

onMounted(() => {
  loadStudents();
  loadSubjects();
});
</script>

<style scoped>
@media (max-width: 640px) {
  .container {
    padding-top: 1rem;
  }
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>