<template>
<div>
    <h2>Счета</h2>
<table class="flat-table">
  <tbody>
    <tr>
      <th>ID</th>
      <th>Номер</th>
      <th>Валюта</th>
      <th>Баланс</th>
    </tr>
    <WalletItem v-for="wallet of wallets" v-bind:wallet="wallet"/>
  </tbody>
</table>
</div>
</template>
<script>
import WalletItem from '@/components/WalletItem'
export default {
    props: ['wallets'],
    components: {
        WalletItem
    }
}
</script>

