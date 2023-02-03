const {createApp} = Vue;


createApp({
    data(){

            return{
                accountData : null,
                isLoadingData: true,
                themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
                activeBar: null,
                clientName: localStorage.getItem("clientName")
            }

    },
    created(){
        this.loadAccount(this.getParamFromUrl("id"));

    },
    methods : {
        getParamFromUrl(param){

            return new URLSearchParams(window.location.search).get(param);
        
        },
        loadAccount(id){

            axios.get("http://localhost:8080/api/accounts/"+id)
                    .then(res => {

                        this.accountData = res.data;

                    })
        },
        
        toggleHandle(){

          this.themeDark = !this.themeDark;

          localStorage.setItem("themeBH", this.themeDark ? "dark" : "light");

        },
        getAnimationBar(barNum){

            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `animatedBar${barNum}Reverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `animatedBar${barNum}`};
      
            }
      
        },
        getAnimationNav(){

            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `navAnimateReverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `navAnimate`};
      
            }
      
          },

    }


}).mount("#app")