import Vue from 'vue';
import Router from 'vue-router';
import Main from "@/components/common/Main";
import Join from "@/components/member/Join";
import Login from "@/components/member/Login";
import ProductList from "@/components/product/ProductList";
import AddProduct from "@/components/product/AddProduct";
import ProductDetail from "@/components/product/ProductDetail";
import BoardDetail from "@/components/board/BoardDetail";
import BoardList from "@/components/board/BoardList";
import AddBoard from "@/components/board/AddBoard";
import Keyword from "@/components/member/Keyword";
import Test from "@/components/chat/Test";
import Chat from "@/components/chat/Chat";
import Profile from "@/components/member/Profile";
import ChatRooms from "@/components/chat/ChatRooms";
import MyProduct from "@/components/member/MyProduct";
import MyLikeProduct from "@/components/member/MyLikeProduct";
import Manner from "@/components/member/Manner";
import Review from "@/components/member/Review";
import Follow from "@/components/member/Follow";

Vue.use(Router); //vue 라우터 사용


export default new Router({ //라우터 연결
    mode: 'history',
    routes: [
        {
            path: '/',
            component: Main
        },
        {
            path: '/join',
            component: Join
        },
        {
            path: '/login',
            component: Login
        },
        {
            path: '/productList',
            component: ProductList
        },
        {
            path: '/addProduct',
            component: AddProduct
        },
        {
            path: '/productDetail',
            component: ProductDetail,
        },
        {
            path: '/boardDetail',
            component: BoardDetail
        },
        {
            path: '/boardList',
            component: BoardList
        },
        {
            path: '/addBoard',
            component: AddBoard
        },
        {
            path: '/keyword',
            component: Keyword
        },
        {
            path: '/test',
            component: Test
        },
        {
            path:'/chat',
            component: Chat,
        },
        {
            path:'/profile',
            component: Profile,
            children:[
                {
                    path:'/profile/myProduct',
                    component: MyProduct
                },
                {
                    path:'/profile/myLikeProduct',
                    component: MyLikeProduct
                },
                {
                    path:'/profile/keyword',
                    component: Keyword
                },
                {
                    path:'/profile/manner',
                    component: Manner
                },
                {
                    path:'/profile/review',
                    component: Review
                },
                {
                    path:'/profile/board',

                },
                {
                    path:'/profile/follow',
                    component: Follow
                }
            ]
        },
        {
            path:'/chatRooms',
            component: ChatRooms
        }
    ]
})