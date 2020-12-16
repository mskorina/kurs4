<template>
  <div id="app">
    <LoginForm @auth-data="Authtry" />
    <AllItem v-bind:dataweb="dataweb"/>
  </div>
</template>

<script>
import LoginForm from '@/components/LoginForm'
import AllItem from '@/components/AllItem'
export default {
  name: 'App',
  data() {
    return {
      tokeninfo:{},
      dataweb:{ 
        
      }
  }
  },
  components: {
    AllItem,
    LoginForm
  },
  methods: {
    Authtry(auth) {
      let tokeninf=this.tokeninfo
      let datawebb=this.dataweb
      this.axios.post('https://api.belcraft.ru/v1/auth', {
          email: auth.username,
          password: auth.password
      })
      .then((response) => {
        this.tokeninfo=response.data
              if (this.tokeninfo.token=="error_password_or_user") {
                alert(
                'Проверьте логин и пароль'
                )
              }
              else {
                this.axios.post('https://api.belcraft.ru/v1/web/data', this.tokeninfo)
                .then((response) => {
                  datawebb=response.data
                  if (datawebb.status==="FAILURE") {
                              alert(
                        'Проверьте существует ли пользователь'
                      )

                    return
                  }
                  else {
                    this.dataweb=response.data
                  }

                })
                .catch( (error) => {
                    console.log(error)
                    alert(
                      'Проблемы с подключением'
                    )
                });                
              }
      })
      .catch( (error) => {
          console.log(error)
          alert(
            'Проблемы с подключением'
          )
      });
      

    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
  
}

</style>

