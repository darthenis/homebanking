const { createApp } = Vue;


createApp({
    data(){
        return{
            data : {
                number: "",
                cvv: "",
                amount : "",
                description : ""
            },
            modalMessage : {
              message: "",
              isError : false
            },
            isLoading : false
        }
    },
    methods: {
        setCurrency(event){

            if(/[0-9]/.test(event.key)){
              
              let number = this.data.number.length;

              if(number < 4 || number > 4 && number < 9 || number > 9 && number < 14 || number > 14 && number < 19 ){

                this.data.number += event.key;

              } else if(number == 4 || number == 9 || number == 14){

                this.data.number += " " + event.key;

              }
           
            } else if(event.key === "Backspace"){

              let number = this.data.number.length;

              if(number < 5 || number > 5 && number < 10 || number > 10 && number < 15 || number > 15 && number <= 19 ){

                this.data.number = this.data.number.slice(0, -1)

              } else if(number == 5 || number == 10 || number == 15){

                this.data.number = this.data.number.slice(0, -2)

              }

            }

            

          },
          setCvv(event){
  
              if(/[0-9]/.test(event.key)){
                
                let number = this.data.cvv.length;
  
                if(number < 3){
  
                  this.data.cvv += event.key;
  
                } 
             
              } else if(event.key === "Backspace"){
  
                this.data.cvv = this.data.cvv.slice(0, -1)

              }
  
            },
            clearForm(){

              this.data = {
                number : "",
                cvv : "",
                amount : "",
                description : ""
              }

            },
            handleMessage(message, seconds, isError){

              this.modalMessage = {
                message : message,
                isError : isError
              }
              
              setTimeout(() => this.modalMessage.message = "", seconds * 1000);

            },
            handlerPay(){

              this.isLoading = true;

              axios.post('http://localhost:8080/api/transactions/pay',{...this.data, amount : parseInt(this.data.amount)})
                .then(res => {

                    this.clearForm();
                    this.handleMessage("Paid succesfully", 3, false)
                    this.isLoading = false;

                }).catch(err => {

                  console.log(err)
                  this.isLoading = false;
                  this.handleMessage(err.response.data, 3, true)

                })

            }

    }


}).mount('#app')