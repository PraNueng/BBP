<template>
  <NavBar/>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-amber-900 to-yellow-900 p-4">
    <BackButton to="/class-management-total" class="pb-10 pl-2" />
    <!-- Header -->
    <div class="max-w-7xl mx-auto">
      <div class="bg-gradient-to-r from-blue-800 to-indigo-500 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          คลาสทั้งหมดในระบบ
        </h1>
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- Tabs -->
        <div class="flex gap-4 mb-8 overflow-x-auto pb-2">
          <button
            @click="activeTab = 'monthly'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'monthly'
                ? 'bg-gradient-to-r from-pink-500 to-pink-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <Calendar class="w-5 h-5" />
            รายเดือน
          </button>
          <button
            @click="activeTab = 'hourly-group'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-group'
                ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <Users class="w-5 h-5" />
            กลุ่มรวม
          </button>
          <button
            @click="activeTab = 'hourly-individual'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-individual'
                ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <User class="w-5 h-5" />
            PV-เดี่ยว / PV-กลุ่ม
          </button>
        </div>

        <FilterAddClass
          :subjects="subjects"
          :grades="grades"
          @filter="handleFilter"
        />

        <!-- Classes Table -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>
        <div v-else-if="getCurrentClasses().length === 0" class="text-center py-12">
          <p class="text-gray-500 text-lg">
            {{ filters.subjectId || filters.gradeId || filters.keyword 
              ? 'ไม่พบคลาสที่ตรงกับเงื่อนไขที่เลือก' 
              : 'ยังไม่มีคลาสเรียน' }}
          </p>
        </div>
        <div v-else class="overflow-x-auto rounded-xl shadow-lg">
          <table class="w-full">
            <thead class="bg-gradient-to-r from-amber-800 via-amber-700 to-yellow-700">
              <tr class="text-center">
                <th class="px-6 py-4 text-sm font-bold text-white">ชื่อคลาส</th>
                <th class="px-6 py-4 text-sm font-bold text-white">วิชา</th>
                <th class="px-6 py-4 text-sm font-bold text-white" v-if="activeTab !== 'hourly-individual'">ประเภท</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ชั้นเรียน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ครูผู้สอน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">นักเรียน (ทั้งหมด)</th>
                <th class="px-6 py-4 text-sm font-bold text-white">สถานะ</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ดูรายละเอียด</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 bg-white">
              <tr
                v-for="cls in filteredClasses"
                :key="cls.id"
                class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-indigo-50 transition-all text-center"
              >
                <td class="px-6 py-4 text-sm font-semibold text-gray-800">
                  {{ cls.className }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-700">
                  {{ cls.subjectName }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-700" v-if="activeTab !== 'hourly-individual'">
                  <span class="px-3 py-1 bg-purple-100 text-purple-700 rounded-full font-medium text-xs">
                    {{ cls.subtypeName }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-700">
                  {{ cls.gradeName }}
                </td>
                
                <!-- แสดงครู -->
                <td class="px-6 py-4 text-sm">
                  <div v-if="activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual'">
                    <div v-if="cls.tutors && cls.tutors.length > 0" class="flex flex-wrap gap-1 justify-center">
                      <span
                        v-for="tutor in cls.tutors.slice(0, 2)"
                        :key="tutor.id"
                        class="px-2 py-1 bg-blue-100 text-blue-700 rounded-full font-medium text-xs"
                      >
                        {{ tutor.nickname }}
                      </span>
                      <span
                        v-if="cls.tutors.length > 2"
                        class="px-2 py-1 bg-gray-100 text-gray-600 rounded-full font-medium text-xs"
                      >
                        +{{ cls.tutors.length - 2 }}
                      </span>
                    </div>
                    <span v-else class="text-gray-400 italic">ยังไม่มีครู</span>
                  </div>
                </td>
                
                <td class="px-6 py-4 text-sm">
                  <span class="px-3 py-1 bg-green-100 text-green-700 rounded-full font-medium text-xs">
                    {{ cls.totalStudents || 0 }} คน
                  </span>
                </td>
                <td class="px-6 py-4 text-sm">
                  <span
                    v-if="cls.isActive"
                    class="text-green-600 font-medium flex items-center justify-center gap-1"
                  >
                    <CheckCircle class="w-4 h-4" />
                    เปิดใช้งาน
                  </span>
                  <span
                    v-else
                    class="text-red-600 font-medium flex items-center justify-center gap-1"
                  >
                    <XCircle class="w-4 h-4" />
                    ปิดใช้งาน
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex justify-center gap-2">
                    <button
                      @click="viewClassDetail(cls)"
                      class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
                    >
                      <Eye class="w-4 h-4" />
                      ดูข้อมูล
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Detail Modal -->
    <ModalShell v-if="showDetailModal && selectedClass" @close="closeDetailModal" :showBackground="true" wrapperClass="bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 w-full max-w-4xl max-h-[90vh] overflow-hidden relative rounded-3xl shadow-2xl">
      <template #icon>
        <div class="p-2 bg-white/20 rounded-lg">
          <Eye class="w-6 h-6 text-white" />
        </div>
      </template>
      <template #title>รายละเอียดคลาส</template>
      <template #closeIcon>
        <X class="w-6 h-6 group-hover:rotate-90 transition-transform" />
      </template>
      <div class="relative z-10 max-h-[calc(90vh-180px)] p-6">
        <div class="space-y-6">
          <!-- Class Info -->
          <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
            <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
              <BookOpen class="w-5 h-5" />
              ข้อมูลคลาส
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-white">
              <div>
                <p class="text-gray-300 text-sm">ชื่อคลาส</p>
                <p class="font-semibold text-lg">{{ selectedClass.className }}</p>
              </div>
              <div>
                <p class="text-gray-300 text-sm">วิชา</p>
                <p class="font-semibold text-lg">{{ selectedClass.subjectName }}</p>
              </div>
              <div v-if="activeTab !== 'hourly-individual'">
                <p class="text-gray-300 text-sm">ประเภท</p>
                <p class="font-semibold text-lg">{{ selectedClass.subtypeName }}</p>
              </div>
              <div>
                <p class="text-gray-300 text-sm">ชั้นเรียน</p>
                <p class="font-semibold text-lg">{{ selectedClass.gradeName }}</p>
              </div>
              
              <!-- แสดงครูตามประเภทคลาส -->
              <div>
                <p class="text-gray-300 text-sm">ครูผู้สอน</p>
                <div v-if="(activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual') && selectedClass.tutors && selectedClass.tutors.length > 0">
                  <p class="font-semibold text-lg">{{ selectedClass.tutors.map(t => t.nickname).join(', ') }}</p>
                </div>
                <p v-else-if="selectedClass.tutorName" class="font-semibold text-lg">{{ selectedClass.tutorName }}</p>
                <p v-else class="font-semibold text-lg text-gray-400">ยังไม่มีครู</p>
              </div>
              
              <div>
                <p class="text-gray-300 text-sm">สถานะ</p>
                <p
                  :class="[
                    'font-semibold text-lg',
                    selectedClass.isActive ? 'text-green-400' : 'text-red-400'
                  ]"
                >
                  {{ selectedClass.isActive ? '✓ เปิดใช้งาน' : '✗ ปิดใช้งาน' }}
                </p>
              </div>
            </div>
          </div>

          <!-- Students List (แสดงทั้ง active และ inactive) -->
          <div
            v-if="activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual'"
            class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm"
          >
            <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
              <Users class="w-5 h-5" />
              รายชื่อนักเรียนทั้งหมด ({{ classStudents.length }} คน)
            </h3>
            
            <!-- แสดงรายชื่อนักเรียน -->
            <div v-if="classStudents.length > 0" class="space-y-2">
              <div
                v-for="(student, index) in classStudents"
                :key="student.id"
                :class="[
                  'rounded-lg p-4 flex items-center justify-between transition-colors',
                  student.isActive === false 
                    ? 'bg-red-500/20 hover:bg-red-500/30' 
                    : 'bg-white/5 hover:bg-white/10'
                ]"
              >
                <div class="flex items-center gap-3">
                  <div 
                    :class="[
                      'w-10 h-10 rounded-full flex items-center justify-center text-white font-bold',
                      student.isActive === false
                        ? 'bg-gradient-to-br from-red-500 to-red-600'
                        : 'bg-gradient-to-br from-blue-500 to-indigo-600'
                    ]"
                  >
                    {{ index + 1 }}
                  </div>
                  <div>
                    <p class="text-white font-semibold">
                      {{ student.studentNickname }}
                    </p>
                    <p class="text-gray-300 text-sm">{{ student.studentName }}</p>
                  </div>
                </div>
                
                <!-- แสดงสถานะ -->
                <div class="text-sm">
                  <span
                    :class="[
                      'px-3 py-1 rounded-full font-medium',
                      student.isActive === false 
                        ? 'bg-red-500/30 text-red-200' 
                        : 'bg-green-500/20 text-green-300'
                    ]"
                  >
                    {{ student.isActive === false ? 'Inactive' : 'Active' }}
                  </span>
                </div>
              </div>
            </div>
            
            <!-- กรณีไม่มีนักเรียน -->
            <div v-else class="bg-white/5 rounded-lg p-4">
              <p class="text-gray-300 text-center">
                ยังไม่มีนักเรียนในคลาสนี้
              </p>
            </div>
          </div>

          <!-- Statistics -->
          <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
            <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
              <Clock class="w-5 h-5" />
              สถิติ
            </h3>
            <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
              <div class="bg-white/5 rounded-lg p-4">
                <p class="text-gray-300 text-sm">นักเรียนทั้งหมด</p>
                <p class="text-2xl font-bold text-white">{{ classStudents.length || 0 }}</p>
              </div>
              <div class="bg-white/5 rounded-lg p-4">
                <p class="text-gray-300 text-sm">นักเรียน Active</p>
                <p class="text-2xl font-bold text-green-400">
                  {{ classStudents.filter(s => s.isActive !== false).length || 0 }}
                </p>
              </div>
              <div class="bg-white/5 rounded-lg p-4">
                <p class="text-gray-300 text-sm">นักเรียน Inactive</p>
                <p class="text-2xl font-bold text-red-400">
                  {{ classStudents.filter(s => s.isActive === false).length || 0 }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="p-6 border-t border-white/5 bg-gradient-to-t from-transparent to-black/10 flex gap-4 justify-end">
          <button
            @click="closeDetailModal"
            class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
          >
            ปิด
          </button>
        </div>
      </template>
    </ModalShell>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import {
  Calendar,
  Users,
  Clock,
  BookOpen,
  User,
  Eye,
  X,
  CheckCircle,
  XCircle
} from 'lucide-vue-next';
import BackButton from '@/components/BackButton.vue';
import NavBar from '@/components/NavBar.vue';
import FilterAddClass from '@/components/FilterAddClass.vue';
import ModalShell from '@/components/ModalShell.vue';
import api from '@/api.js';

// State
const activeTab = ref('monthly');
const isLoading = ref(false);
const showDetailModal = ref(false);
const selectedClass = ref(null);

// Data
const monthlyClasses = ref([]);
const hourlyGroupClasses = ref([]);
const hourlyIndividualClasses = ref([]);
const subjects = ref([]);
const grades = ref([]);
const classStudents = ref([]);

const filters = ref({
  subjectId: null,
  gradeId: null,
  keyword: ''
});

// Methods
const loadData = async () => {
  isLoading.value = true;
  try {
    const [subjectsRes, gradesRes] = await Promise.all([
      api.get('/subjects'),
      api.get('/grades')
    ]);

    subjects.value = subjectsRes.data;
    grades.value = gradesRes.data;

    await loadClasses();
  } catch (error) {
    console.error('Error loading data:', error);
  } finally {
    isLoading.value = false;
  }
};

const loadClasses = async () => {
  try {
    if (activeTab.value === 'monthly') {
      const res = await api.get('/monthly-classes');
      monthlyClasses.value = await Promise.all(
        res.data.map(async (cls) => {
          try {
            const studentsRes = await api.get(`/enrollments/monthly/${cls.id}/all-students`);
            // แสดงนักเรียนทั้งหมด (ไม่ filter)
            return {
              ...cls,
              totalStudents: studentsRes.data.length,
              isActive: studentsRes.data.some(s => s.isActive !== false)
            };
          } catch (error) {
            console.error(`Error loading students for class ${cls.id}:`, error);
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    } else if (activeTab.value === 'hourly-group') {
    const res = await api.get('/hourly-group-classes');
    hourlyGroupClasses.value = await Promise.all(
        res.data.map(async (cls) => {
        try {
            const studentsRes = await api.get(`/enrollments/hourly-group/${cls.id}/all-students`);
            // แสดงนักเรียนทั้งหมด (ไม่ filter)
            return {
              ...cls,
              totalStudents: studentsRes.data.length,
              isActive: studentsRes.data.some(s => s.isActive !== false)
            };
          } catch (error) {
            console.error(`Error loading students for class ${cls.id}:`, error);
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    } else {
      const res = await api.get('/hourly-individual-classes');
      hourlyIndividualClasses.value = await Promise.all(
        res.data.map(async (cls) => {
          const legacyTutor = cls.tutorId ? [{ id: cls.tutorId, nickname: cls.tutorName }] : [];

          try {
            const tutorsResponse = await api.get(`/hourly-individual-classes/${cls.id}/tutors`);
            if (tutorsResponse.data && tutorsResponse.data.length > 0) {
              cls.tutors = tutorsResponse.data;
              cls.totalTutors = tutorsResponse.data.length;
            } else if (legacyTutor.length > 0) {
              cls.tutors = legacyTutor;
              cls.totalTutors = legacyTutor.length;
            } else {
              cls.tutors = [];
              cls.totalTutors = 0;
            }

            const studentsRes = await api.get(`/enrollments/hourly-individual/${cls.id}/all-students`);
            // แสดงนักเรียนทั้งหมด (ไม่ filter)
            
            return {
              ...cls,
              students: studentsRes.data,
              totalStudents: studentsRes.data.length,
              isActive: studentsRes.data.some(s => s.isActive !== false)
            };
          } catch (error) {
            if (legacyTutor.length > 0) {
              cls.tutors = legacyTutor;
              cls.totalTutors = legacyTutor.length;
            } else {
              console.warn(`Cannot fetch tutors for class ${cls.id}:`, error);
              cls.tutors = [];
              cls.totalTutors = 0;
            }
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    }
  } catch (error) {
    console.error('Error loading classes:', error);
  }
};

const loadClassStudents = async (classId, classType) => {
  try {
    let endpoint = '';
    if (classType === 'monthly') {
    endpoint = `/enrollments/monthly/${classId}/all-students`;
    } else if (classType === 'hourly-group') {
    endpoint = `/enrollments/hourly-group/${classId}/all-students`;
    } else if (classType === 'hourly-individual') {
    endpoint = `/enrollments/hourly-individual/${classId}/all-students`;
    } else {
      return;
    }
    
    const res = await api.get(endpoint);
    // แสดงนักเรียนทั้งหมด (ไม่ filter)
    classStudents.value = res.data;
  } catch (error) {
    console.error('Error loading class students:', error);
    classStudents.value = [];
  }
};

const getCurrentClasses = () => {
  if (activeTab.value === 'monthly') return monthlyClasses.value;
  if (activeTab.value === 'hourly-group') return hourlyGroupClasses.value;
  return hourlyIndividualClasses.value;
};

const filteredClasses = computed(() => {
  let classes = getCurrentClasses();
  
  if (filters.value.subjectId) {
    classes = classes.filter(c => c.subjectId === filters.value.subjectId);
  }
  
  if (filters.value.gradeId) {
    classes = classes.filter(c => {
      if (c.gradeId) {
        return c.gradeId === filters.value.gradeId;
      }
      
      if (activeTab.value === 'hourly-individual' && c.students) {
        return c.students.some(s => s.gradeId === filters.value.gradeId);
      }
      
      return false;
    });
  }
  
  if (filters.value.keyword) {
    const keyword = filters.value.keyword.toLowerCase();
    classes = classes.filter(c => 
      c.className?.toLowerCase().includes(keyword) ||
      c.subjectName?.toLowerCase().includes(keyword) ||
      c.gradeName?.toLowerCase().includes(keyword) ||
      c.tutorName?.toLowerCase().includes(keyword) ||
      (c.tutors && c.tutors.some(t => t.nickname?.toLowerCase().includes(keyword)))
    );
  }
  
  return classes;
});

const handleFilter = (newFilters) => {
  filters.value = { ...filters.value, ...newFilters };
};

const viewClassDetail = async (cls) => {
  selectedClass.value = cls;
  
  if (activeTab.value === 'monthly' || activeTab.value === 'hourly-group' || activeTab.value === 'hourly-individual') {
    await loadClassStudents(cls.id, activeTab.value);
  }
  
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedClass.value = null;
  classStudents.value = [];
};

// Lifecycle
onMounted(() => {
  loadData();
});

// Watchers
watch(activeTab, () => {
  if (activeTab.value) {
    loadClasses();
  }
});
</script>

<style scoped>
</style>