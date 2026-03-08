import useJwt from '@/auth/jwt/useJwt'
import store from '@/store';

const service = useJwt;

export default function request(...arg){
    
    //추가적인 파라미터 처리
    const logRouteChange = store.state.coching.logRouteChange || {
        from : null,
        to : null
      };
    const currentLogRoute = logRouteChange.to || {};
    const {data} = arg[0];
    if(data){
        data["routerName"] = currentLogRoute.name;
    }
    
    return service.axiosIns(...arg);
}
