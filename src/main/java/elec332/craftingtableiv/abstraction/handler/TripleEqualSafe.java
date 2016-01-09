package elec332.craftingtableiv.abstraction.handler;

import java.util.UUID;

/**
 * Created by Elec332 on 5-7-2015.
 */
public class TripleEqualSafe {

    public static TripleEqualSafe newKey(){
        return new TripleEqualSafe();
    }

    private TripleEqualSafe(){
        this.uuid1 = UUID.randomUUID();
        this.uuid2 = UUID.randomUUID();
        this.uuid3 = UUID.randomUUID();
        this.hash = uuid1.hashCode()*uuid2.hashCode()+uuid3.hashCode()*UUID.randomUUID().hashCode();
    }

    private UUID uuid1;
    private UUID uuid2;
    private UUID uuid3;
    private int hash;

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TripleEqualSafe && uuid1.equals(((TripleEqualSafe) obj).uuid1) && uuid2.equals(((TripleEqualSafe) obj).uuid2) && uuid3.equals(((TripleEqualSafe) obj).uuid3);
    }
}
