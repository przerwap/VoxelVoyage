package com.thevoxelbox.voyage.commands;

import com.thevoxelbox.voyage.repositories.VoyageRepository;
import com.thevoxelbox.voyage.services.VoyageServices;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.bukkit.command.CommandSender;

import javax.xml.ws.Response;

public class Create extends CmdHandler implements Injection {
    private VoyageServices voyageServices = injector.getInstance(VoyageServices.class);

    Options otpions = new Options()
            .addOption("p", "password", true,"sets password for flight");
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length >= 1) {
            CommandLine cmdLine = null;
            try {
                cmdLine = parser.parse(otpions, args);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (args[0].matches("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")) {
                Response response = voyageServices.createNewPath()

            } else {

            }



        return false;
    }
}
