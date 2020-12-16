<template>
    <tr>
      <td>{{wallet.walletid}}</td>
      <td>{{wallet.name}}</td>
      <td>{{wallet.typemoney}}</td>
      <td>{{wallet.money}}</td>
    </tr>
</template>
<script>
export default {
    props: {
        wallet: {
            type: Object,
            required: true
        }
    }
}
</script>
