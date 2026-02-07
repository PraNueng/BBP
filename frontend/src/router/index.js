import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import HomeTutor from '@/views/HomeTutor.vue'
import HourForm from '@/views/HourForm.vue'
import EditHourForm from '@/views/EditHourForm.vue'
import HomeAdmin from '@/views/HomeAdmin.vue'
import LearningHistoryGrade from '@/views/LearningHistoryGrade.vue'
import StudentManagement from '@/views/StudentManagement.vue'
import StudentDetail from '@/modals/StudentDetail.vue'
import StudentManagementLikeTutor from '@/views/StudentManagementLikeTutor.vue'
import MathForm from '@/views/MathForm.vue'
import ScienceForm from '@/views/ScienceForm.vue'
import HomeTutorCont from '@/views/HomeTutorCont.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/login',
      redirect: '/'
    },
    {
      path: '/home-tutor',
      name: 'HomeTutor',
      component: HomeTutor,
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/home-tutor-cont',
      name: 'HomeTutorCont',
      component: HomeTutorCont,
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/home-admin',
      name: 'HomeAdmin',
      component: HomeAdmin,
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/hour-forms',
      name: 'HourForm',
      component: HourForm,
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/math-forms',
      name: 'MathForm',
      component: MathForm,
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/science-forms',
      name: 'ScienceForm',
      component: ScienceForm,
      meta: { requiresAuth: true , role: 'tutor' }
    },
    {
      path: '/list-forms',
      name: 'ListForm',
      component: () => import('@/views/ListForm.vue'),
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/list-hour-forms',
      name: 'ListHourFormTutor',
      component: () => import('@/views/ListHourFormTutor.vue'),
      meta: { requiresAuth: true, role: 'tutor'   }
    },
    {
      path: '/list-hour-form-tutor',
      name: 'ListHourFormTutorAdmin',
      component: () => import('@/views/ListHourFormTutor.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/list-hour-form-edit/:id',
      name: 'EditHourForm',
      component: () => import('@/views/EditHourForm.vue'),
      meta: { requiresAuth: true, role: 'tutor' }
    },
        {
      path: '/list-math-form-edit/:id',
      name: 'EditMathForm',
      component: () => import('@/views/EditMathForm.vue'),
      meta: { requiresAuth: true, role: 'tutor' }
    },
        {
      path: '/list-science-form-edit/:id',
      name: 'EditScienceForm',
      component: () => import('@/views/EditScienceForm.vue'),
      meta: { requiresAuth: true, role: 'tutor' }
    },
    {
      path: '/learning-history-grades',
      name: 'LearningHistoryGrades',
      component: LearningHistoryGrade,
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/learning-history-grade-:grade',
      name: 'LearningHistoryListStudent',
      component: () => import('@/views/LearningHistoryListStudent.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/list-hour-form-student',
      name: 'ListHourFormStudent',
      component: () => import('@/views/ListHourFormStudent.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/student-management',
      name: 'StudentManagement',
      component: () => import('@/views/StudentManagement.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/student-management-like-tutor',
      name: 'StudentManagementLikeTutor',
      component: () => import('@/views/StudentManagementLikeTutor.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/student-classes/:id',
      name: 'StudentClassView.vue',
      component: () => import('@/views/StudentClassview.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/student-management-total',
      name: 'StudentManagementTotal',
      component: () => import('@/views/StudentManagementTotal.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/tutor-management',
      name: 'TutorsManagement',
      component: () => import('@/views/TutorManagement.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/tutor-management-total',
      name: 'TutorManagementTotal',
      component: () => import('@/views/TutorManagementTotal.vue'),
      meta: { requiresAuth: true, role: 'admin'  }
    },
    {
      path: '/class-management',
      name: 'ClassManagement',
      component: () => import('@/views/ClassManagement.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/class-management-total',
      name: 'ClassManagementTotal',
      component: () => import('@/views/ClassManagementTotal.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/view-all-classes',
      name: 'ViewAllClasses',
      component: () => import('@/views/ViewAllClasses.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/students/:id',
      name: 'StudentDetail',
      component: StudentDetail,
      props: true
    },
    {
      path: '/add-classes',
      name: 'AddClass',
      component: () => import('@/views/AddClass.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/tutor-classes/:id',
      name: 'TutorClassView.vue',
      component: () => import('@/views/TutorClassesView.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/learning-history-grade-m1',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm1' } })
    },
    {
      path: '/learning-history-grade-m2',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm2' } })
    },
    {
      path: '/learning-history-grade-m3',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm3' } })
    },
    {
      path: '/learning-history-grade-m4',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm4' } })
    },
    {
      path: '/learning-history-grade-m5',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm5' } })
    },
    {
      path: '/learning-history-grade-m6',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'm6' } })
    },
    {
      path: '/learning-history-grade-others',
      redirect: to => ({ name: 'LearningHistoryListStudent', params: { grade: 'others' } })
    },
    {
      path: '/students',
      name: 'StudentManagement',
      component: () => import('@/views/StudentManagement.vue'),
      meta: { requiresAuth: true, role: 'admin' }
    },
    {
      path: '/notifications',
      name: 'Notifications',
      component: () => import('@/views/Notification/Notifications.vue'),
      meta: { requiresAuth: true, role: 'admin'  }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  const user = JSON.parse(sessionStorage.getItem('user') || localStorage.getItem('user') || '{}')
  const userRole = user.role

  // 1. ถ้าหน้าต้องการ auth แต่ไม่มี token ให้ไปหน้า login
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  // 2. ถ้ามี token แล้วพยายามเข้าหน้า login -> redirect ตาม role
  if ((to.path === '/' || to.path === '/login') && token) {
    if (userRole === 'tutor') return next('/home-tutor')
    if (userRole === 'admin') return next('/home-admin')
    return next('/') // fallback
  }

  // 3. ตรวจสอบ role ของหน้าที่จะเข้า (ส่วนที่ขาดหายไป!)
  if (to.meta.requiresAuth && to.meta.role) {
    if (to.meta.role !== userRole) {
      // บล็อกการเข้าถึง - redirect ไปหน้า home ของ role ตัวเอง
      if (userRole === 'tutor') return next('/home-tutor')
      if (userRole === 'admin') return next('/home-admin')
      return next('/login') // fallback ถ้าไม่มี role
    }
  }

  next()
})

export default router
