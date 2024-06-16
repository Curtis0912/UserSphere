export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user', routes: [
          {name: '登录', path: '/user/login', component: './user/Login'},
          {name: '注册', path: '/user/register', component: './user/Register'},
        ]
      },
      {component: './404'}],
  },
  {path: '/welcome', name:'欢迎', icon: 'smile', component: './Welcome'},
  {
    path: '/admin',
    name: '管理',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
    routes: [
      {path: '/admin/user-manage', name: '用户管理', icon: 'smile', component: './Admin/UserManage'},
      {path: '/admin/sub-page', icon: 'smile', component: './Welcome'},
      {component: './404'},
    ],
  },
  {icon: 'table', name:'查询表格', path: '/list', component: './TableList'},
  {path: '/', redirect: '/welcome'},
  {component: './404'},
];
