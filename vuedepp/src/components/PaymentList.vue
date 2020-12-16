<template>
<div>
    <h2> Платежи</h2>
<table class="flat-table">
  <tbody>
    <tr>
      <th>ID</th>
      <th>ID Кошелька</th>
      <th>Тип</th>
      <th>Описание</th>
      <th>Дата</th>
    </tr>    
    <PaymentItem v-for="payment of payments" v-bind:payment="payment"/>
  </tbody>
</table>
</div>
</template>
<script>
import PaymentItem from '@/components/PaymentItem'
export default {
    props: ['payments'],
    components: {
        PaymentItem
    }
}
</script>