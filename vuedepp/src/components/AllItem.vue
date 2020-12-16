<template>
    <div>
        <WalletList v-bind:wallets="dataweb.wallets"/>
        <PaymentList v-bind:payments="dataweb.payments"/>
    </div>
</template>
<script>
import WalletList from '@/components/WalletList'
import PaymentList from '@/components/PaymentList'
export default {
    props: ['dataweb'],
    components: {
        WalletList,
        PaymentList
    }
}
</script>

<style>
.flat-table {
  display:table;
  font-family: sans-serif;
  -webkit-font-smoothing: antialiased;
  font-size: 85%;
  overflow:visible;
  width: auto;
}
.flat-table th {
  background-color: #70c469;
  color: white;
  font-weight: normal;
  padding: 10px 30px;
  text-align: center;
}
.flat-table td {
  background-color: #eee;
  color: #6f6f6f;
  padding: 10px 20px;
}

.flat-table{
    text-align:center; 
    margin-left:auto; 
    margin-right:auto; 
    width:100px
}

</style>