/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.export;

import net.minecraft.entity.EntityType;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class MobData
{
    private String Name;
    private String EnglishName;
    //private String mod;
    private String RegistryName;
    private String Icon;
    private transient EntityType mob;

    public MobData(EntityType Entitymob)
    {
        Name = null;
        EnglishName = null;
        //mod = Entitymob.getRegistryName().getNamespace();
        RegistryName = Entitymob.getRegistryName().toString();
        Icon = ExportUtils.getEntityIcon(Entitymob);
        this.mob = Entitymob;
    }

    public EntityType getMob()
    {
        return mob;
    }

    public void setName(String name)
    {
        this.Name = name;
    }

    public void setEnglishname(String name)
    {
        this.EnglishName = name;
    }
}
